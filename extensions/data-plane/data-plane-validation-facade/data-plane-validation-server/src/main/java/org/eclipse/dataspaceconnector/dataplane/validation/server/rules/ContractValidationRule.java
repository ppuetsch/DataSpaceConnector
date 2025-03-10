package org.eclipse.dataspaceconnector.dataplane.validation.server.rules;

import com.nimbusds.jwt.JWTClaimsSet;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.store.ContractNegotiationStore;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.agreement.ContractAgreement;
import org.eclipse.dataspaceconnector.token.spi.ValidationRule;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.time.Instant;

public class ContractValidationRule implements ValidationRule {

    private final ContractNegotiationStore contractNegotiationStore;

    public ContractValidationRule(ContractNegotiationStore contractNegotiationStore) {
        this.contractNegotiationStore = contractNegotiationStore;
    }

    @Override
    public Result<JWTClaimsSet> checkRule(@NotNull JWTClaimsSet toVerify) {
        String contractId;
        try {
            contractId = toVerify.getStringClaim("cid");
        } catch (ParseException e) {
            return Result.failure("Failed to parse claims");
        }

        if (contractId == null) {
            return Result.failure("Missing contract id claim `cid`");
        }

        ContractAgreement contractAgreement = contractNegotiationStore.findContractAgreement(contractId);
        if (contractAgreement == null) {
            return Result.failure("No contract agreement found for id: " + contractId);
        }

        // check contract expiration date
        if (Instant.now().isAfter(Instant.ofEpochSecond(contractAgreement.getContractEndDate()))) {
            return Result.failure("Contract has expired");
        }

        return Result.success(toVerify);
    }
}
