package com.hotmokafe.application.blockchain;

import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.StringUtils;
import io.hotmoka.beans.references.TransactionReference;
import io.hotmoka.beans.requests.ConstructorCallTransactionRequest;
import io.hotmoka.beans.requests.InstanceMethodCallTransactionRequest;
import io.hotmoka.beans.requests.SignedTransactionRequest;
import io.hotmoka.beans.requests.SignedTransactionRequest.Signer;
import io.hotmoka.beans.signatures.CodeSignature;
import io.hotmoka.beans.signatures.ConstructorSignature;
import io.hotmoka.beans.signatures.NonVoidMethodSignature;
import io.hotmoka.beans.types.ClassType;
import io.hotmoka.beans.values.*;
import io.hotmoka.crypto.SignatureAlgorithm;
import io.hotmoka.crypto.SignatureAlgorithmForTransactionRequests;
import io.hotmoka.nodes.GasHelper;
import io.hotmoka.nodes.Node;
import io.hotmoka.nodes.NonceHelper;
import io.hotmoka.remote.RemoteNode;


import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Base64;

import static io.hotmoka.beans.Coin.panarea;


public class CreateAccount extends AbstractCommand {

    //"the url of the node (without the protocol)"
    private String url = "ec2-54-194-239-91.eu-west-1.compute.amazonaws.com:8080";

    //"the reference to the account that pays for the creation, or the string "faucet"
    private String payer;

    //"the initial balance of the account"
    private BigInteger balance;

    //"the initial red balance of the account"
    private BigInteger balanceRed;

    //"runs in non-interactive mode"
    private boolean nonInteractive;

    private CreateAccount() {
    }

    public CreateAccount(String url, String payer, String balance, String balanceRed) throws CommandException {
        try {
            if (StringUtils.isValid(url) && StringUtils.isValid(payer)
                    && StringUtils.isValid(balance)) {
                this.url = url;
                this.payer = payer;
                this.nonInteractive = !payer.equalsIgnoreCase("faucet");
                this.balance = new BigInteger(balance);
                this.balanceRed = new BigInteger(balanceRed);
            } else
                throw new CommandException(new IllegalArgumentException("Campi non valorizzati correttamente"));
        } catch (NumberFormatException e) {
            throw new CommandException(e);
        }
    }

    public CreateAccount(String payer, String balance, String balanceRed) {
        this("ec2-54-194-239-91.eu-west-1.compute.amazonaws.com:8080", payer, balance, balanceRed);
    }

    @Override
    protected void execute() throws Exception {
        Store.getInstance().setUrl(this.url);
        Store.getInstance().getCurrentAccount().setReference(new Run().getOutcome());
    }

    private class Run {
        private final Node node;
        private final SignatureAlgorithm<SignedTransactionRequest> signature;
        private final KeyPair keys;
        private final String publicKey;
        private final NonceHelper nonceHelper;
        private final GasHelper gasHelper;
        private final StorageReference account;
        private final StorageReference manifest;
        private final TransactionReference takamakaCode;
        private final String chainId;

        private Run() throws Exception {
            try (Node node = this.node = RemoteNode.of(remoteNodeConfig(url))) {
                signature = SignatureAlgorithmForTransactionRequests.mk(node.getNameOfSignatureAlgorithmForRequests());
                keys = signature.getKeyPair();
                publicKey = Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
                manifest = node.getManifest();
                takamakaCode = node.getTakamakaCode();
                chainId = ((StringValue) node.runInstanceMethodCallTransaction(new InstanceMethodCallTransactionRequest
                        (manifest, _100_000, takamakaCode, CodeSignature.GET_CHAIN_ID, manifest))).value;
                nonceHelper = new NonceHelper(node);
                gasHelper = new GasHelper(node);
                account = createAccount();
                dumpKeysOfAccount();
            }
        }

        public String getOutcome() {
            return account.toString();
        }

        private void dumpKeysOfAccount() throws IOException {
            String fileName = dumpKeys(account, keys);
            System.out.println("The keys of the account have been saved into the file " + fileName);
        }

        private StorageReference createAccount() throws Exception {
            return "faucet".equalsIgnoreCase(payer) ? createAccountFromFaucet() : createAccountFromPayer();
        }

        private StorageReference createAccountFromFaucet() throws Exception {
            System.out.println("Free account creation from faucet will succeed only if the gamete of the node supports an open unsigned faucet");

            StorageReference gamete = (StorageReference) node.runInstanceMethodCallTransaction(new InstanceMethodCallTransactionRequest
                    (manifest, _100_000, takamakaCode, CodeSignature.GET_GAMETE, manifest));

            return (StorageReference) node.addInstanceMethodCallTransaction(new InstanceMethodCallTransactionRequest
                    (Signer.with(signature, keys), gamete, nonceHelper.getNonceOf(gamete),
                            chainId, _100_000, gasHelper.getSafeGasPrice(), takamakaCode,
                            new NonVoidMethodSignature(ClassType.GAMETE, "faucet", ClassType.EOA, ClassType.BIG_INTEGER, ClassType.BIG_INTEGER, ClassType.STRING),
                            gamete,
                            new BigIntegerValue(balance), new BigIntegerValue(balanceRed), new StringValue(publicKey)));
        }

        private StorageReference createAccountFromPayer() throws Exception {
            StorageReference payer = new StorageReference(CreateAccount.this.payer);
            KeyPair keysOfPayer = readKeys(payer);
            Signer signer = Signer.with(signature, keysOfPayer);

            StorageReference account = node.addConstructorCallTransaction(new ConstructorCallTransactionRequest
                    (signer, payer, nonceHelper.getNonceOf(payer),
                            chainId, _100_000, gasHelper.getSafeGasPrice(), takamakaCode,
                            new ConstructorSignature(ClassType.EOA, ClassType.BIG_INTEGER, ClassType.STRING),
                            new BigIntegerValue(balance), new StringValue(publicKey)));

            if (balanceRed.signum() > 0)
                // we send the red coins if required
                node.addInstanceMethodCallTransaction(new InstanceMethodCallTransactionRequest
                        (signer, payer, nonceHelper.getNonceOf(payer), chainId, _100_000, gasHelper.getSafeGasPrice(), takamakaCode,
                                CodeSignature.RECEIVE_RED_BIG_INTEGER, account, new BigIntegerValue(balanceRed)));

            return account;
        }
    }
}
