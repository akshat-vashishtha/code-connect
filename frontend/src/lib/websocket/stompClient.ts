import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { SubmissionResultResponse } from '@/types/submission';

export class StompClientManager {
  private client: Client | null = null;
  private isConnected = false;
  private pendingSubscriptions: Array<{
    topic: string;
    callback: (result: SubmissionResultResponse) => void;
  }> = [];
  private activeSubscriptions = new Map<string, StompSubscription>();

  constructor() {
    this.initClient();
  }

  private initClient(): void {
    if (typeof window === 'undefined') return;

    const brokerUrl = process.env.NEXT_PUBLIC_WS_URL || '/ws-connect';

    this.client = new Client({
      webSocketFactory: () => {
        // Fallback to SockJS for standard compatibility
        return new SockJS(brokerUrl) as unknown as WebSocket;
      },
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: (str) => {
        if (process.env.NODE_ENV !== 'production') {
          console.debug('[STOMP]', str);
        }
      },
      onConnect: () => {
        this.isConnected = true;
        this.flushPendingSubscriptions();
      },
      onDisconnect: () => {
        this.isConnected = false;
        this.activeSubscriptions.clear();
      },
      onStompError: (frame) => {
        console.error('[STOMP Error]', frame.headers['message'], frame.body);
      },
    });

    this.client.activate();
  }

  private flushPendingSubscriptions(): void {
    while (this.pendingSubscriptions.length > 0) {
      const item = this.pendingSubscriptions.shift();
      if (item) {
        this.subscribeToTopic(item.topic, item.callback);
      }
    }
  }

  public subscribeToSubmission(
    submissionId: string,
    callback: (result: SubmissionResultResponse) => void
  ): () => void {
    const topic = `/topic/submissions.${submissionId}`;
    return this.subscribeToTopic(topic, callback);
  }

  private subscribeToTopic(
    topic: string,
    callback: (result: SubmissionResultResponse) => void
  ): () => void {
    if (!this.client || !this.isConnected) {
      this.pendingSubscriptions.push({ topic, callback });
      return () => {
        this.pendingSubscriptions = this.pendingSubscriptions.filter(
          (sub) => sub.topic !== topic
        );
      };
    }

    if (this.activeSubscriptions.has(topic)) {
      this.activeSubscriptions.get(topic)?.unsubscribe();
    }

    const sub = this.client.subscribe(topic, (message: IMessage) => {
      try {
        const parsed: SubmissionResultResponse = JSON.parse(message.body);
        callback(parsed);
      } catch (err) {
        console.error('[STOMP] Failed to parse message body', err);
      }
    });

    this.activeSubscriptions.set(topic, sub);

    return () => {
      sub.unsubscribe();
      this.activeSubscriptions.delete(topic);
    };
  }

  public disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.isConnected = false;
    }
  }
}

// Singleton STOMP client instance for client-side usage
let stompInstance: StompClientManager | null = null;

export function getStompClient(): StompClientManager {
  if (!stompInstance) {
    stompInstance = new StompClientManager();
  }
  return stompInstance;
}
