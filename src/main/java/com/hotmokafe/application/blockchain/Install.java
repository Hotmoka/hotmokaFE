package com.hotmokafe.application.blockchain;
import com.hotmokafe.application.utils.Store;
import com.hotmokafe.application.utils.ListUtils;
import com.hotmokafe.application.utils.StringUtils;
import io.hotmoka.beans.references.LocalTransactionReference;
import io.hotmoka.beans.references.TransactionReference;
import io.hotmoka.beans.requests.InstanceMethodCallTransactionRequest;
import io.hotmoka.beans.requests.JarStoreTransactionRequest;
import io.hotmoka.beans.requests.SignedTransactionRequest;
import io.hotmoka.beans.signatures.CodeSignature;
import io.hotmoka.beans.values.StorageReference;
import io.hotmoka.beans.values.StringValue;
import io.hotmoka.nodes.GasHelper;
import io.hotmoka.nodes.Node;
import io.hotmoka.nodes.NonceHelper;
import io.hotmoka.remote.RemoteNode;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.List;
import java.util.stream.Stream;

public class Install extends AbstractCommand {

    private String url;

    private String payer;

    private byte[] jar;

    private List<String> libs;

    private String classpath;

    private boolean nonInteractive;

    private String gasLimit;

    //extra

    private String outcome;

    public Install(String url, String payer, byte[] jar, List<String> libs, String classpath, boolean nonInteractive, String gasLimit) {
        this.url = StringUtils.isValid(url) ? url : Store.getInstance().getUrl();
        this.payer = StringUtils.isValid(payer) ? payer : Store.getInstance().getCurrentAccount().getReference();

        if(jar != null)
            this.jar = jar;
        else
            throw new CommandException("The jar file is empty.");

        this.libs = ListUtils.isValid(libs) ? libs : null;
        this.classpath = StringUtils.isValid(classpath) ? classpath : "takamakaCode";
        this.nonInteractive = nonInteractive;
        this.gasLimit = StringUtils.isValid(gasLimit) ? gasLimit : "heuristic";
    }

    public String getOutcome() {
        return outcome;
    }

    @Override
    protected void execute() throws Exception {
        outcome = new Run().getOutcome();
    }

    private class Run {
        private final JarStoreTransactionRequest request;
        private String outcome;

        private Run() throws Exception {
            try (Node node = RemoteNode.of(remoteNodeConfig(url))) {
                TransactionReference takamakaCode = node.getTakamakaCode();
                StorageReference manifest = node.getManifest();
                StorageReference payer = new StorageReference(Install.this.payer);
                String chainId = ((StringValue) node.runInstanceMethodCallTransaction(new InstanceMethodCallTransactionRequest
                        (manifest, _100_000, takamakaCode, CodeSignature.GET_CHAIN_ID, manifest))).value;
                GasHelper gasHelper = new GasHelper(node);
                NonceHelper nonceHelper = new NonceHelper(node);
                KeyPair keys = readKeys(payer);
                TransactionReference[] dependencies;
                if (libs != null)
                    dependencies = Stream.concat(libs.stream().map(LocalTransactionReference::new), Stream.of(takamakaCode))
                            .distinct().toArray(TransactionReference[]::new);
                else
                    dependencies = new TransactionReference[] { takamakaCode };

                BigInteger gas = "heuristic".equals(gasLimit) ? _100_000.add(BigInteger.valueOf(100).multiply(BigInteger.valueOf(jar.length))) : new BigInteger(gasLimit);
                TransactionReference classpath = "takamakaCode".equals(Install.this.classpath) ?
                        takamakaCode : new LocalTransactionReference(Install.this.classpath);

                this.request = new JarStoreTransactionRequest(
                        SignedTransactionRequest.Signer.with(node.getSignatureAlgorithmForRequests(), keys),
                        payer,
                        nonceHelper.getNonceOf(payer),
                        chainId,
                        gas,
                        gasHelper.getGasPrice(),
                        classpath,
                        jar,
                        dependencies);

                try {
                    TransactionReference response = node.addJarStoreTransaction(request);
                    outcome =   "<%REPLACE%> has been installed at " + response.toString();
                }
                finally {
                    printCosts(node, request);
                }
            }
        }

        public String getOutcome() {
            return outcome;
        }
    }
}