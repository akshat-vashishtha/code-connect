package com.codeconnect.user.infrastructure.security.chain;

import com.codeconnect.user.infrastructure.security.chain.handler.AuthenticationEstablishmentHandler;
import com.codeconnect.user.infrastructure.security.chain.handler.SignatureValidationHandler;
import com.codeconnect.user.infrastructure.security.chain.handler.TimestampValidationHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Assembles the ordered security validation Chain of Responsibility at startup.
 * Follows the Builder/Assembler pattern: constructs the chain once and reuses it per request.
 * New handlers are plugged in by changing this single assembly point.
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
