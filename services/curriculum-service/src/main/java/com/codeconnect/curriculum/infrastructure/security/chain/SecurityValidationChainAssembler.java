package com.codeconnect.curriculum.infrastructure.security.chain;

import com.codeconnect.curriculum.infrastructure.security.chain.handler.AuthenticationEstablishmentHandler;
import com.codeconnect.curriculum.infrastructure.security.chain.handler.SignatureValidationHandler;
import com.codeconnect.curriculum.infrastructure.security.chain.handler.TimestampValidationHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Assembles the ordered security validation Chain of Responsibility at startup for curriculum-service.
 * TimestampValidationHandler → SignatureValidationHandler → AuthenticationEstablishmentHandler
 */
@Component
@RequiredArgsConstructor
public class SecurityValidationChainAssembler {

    private final TimestampValidationHandler timestampHandler;
    private final SignatureValidationHandler signatureHandler;
    private final AuthenticationEstablishmentHandler authHandler;

    private SecurityValidationHandler chainHead;

    @PostConstruct
    public void assemble() {
        timestampHandler.setNext(signatureHandler).setNext(authHandler);
        chainHead = timestampHandler;
    }

    public SecurityValidationHandler getChain() {
        return chainHead;
    }
}
