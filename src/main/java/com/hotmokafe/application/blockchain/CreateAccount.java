package com.hotmokafe.application.blockchain;

import com.hotmokafe.application.entities.Person;
import io.hotmoka.beans.requests.ConstructorCallTransactionRequest;
import io.hotmoka.beans.requests.InstanceMethodCallTransactionRequest;
import io.hotmoka.beans.requests.SignedTransactionRequest;
import io.hotmoka.beans.signatures.ConstructorSignature;
import io.hotmoka.beans.signatures.NonVoidMethodSignature;
import io.hotmoka.beans.types.ClassType;
import io.hotmoka.beans.values.IntValue;
import io.hotmoka.beans.values.StorageReference;
import io.hotmoka.beans.values.StorageValue;
import io.hotmoka.beans.values.StringValue;
import io.hotmoka.nodes.ConsensusParams;
import io.hotmoka.nodes.GasHelper;
import io.hotmoka.nodes.Node;
import io.hotmoka.nodes.views.InitializedNode;
import io.hotmoka.nodes.views.NodeWithAccounts;
import io.hotmoka.nodes.views.NodeWithJars;
import io.hotmoka.tendermint.TendermintBlockchain;
import io.hotmoka.tendermint.TendermintBlockchainConfig;


import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static io.hotmoka.beans.Coin.panarea;
import static io.hotmoka.beans.types.BasicTypes.INT;
import static java.math.BigInteger.ZERO;

public class CreateAccount {
    public final static BigInteger GREEN_AMOUNT = BigInteger.valueOf(100_000_000);
    public final static BigInteger RED_AMOUNT = BigInteger.ZERO;
    private final static ClassType PERSON = new ClassType("io.takamaka.family.Person");

    public static String Run(Person person) {
        String result = "errore";

        TendermintBlockchainConfig config = new TendermintBlockchainConfig.Builder().build();
        ConsensusParams consensus;
        try {
            consensus = new ConsensusParams.Builder().build();
        } catch (NoSuchAlgorithmException e) {
            return result;
        }

        Path takamakaCodePath = Paths.get
                ("modules/io-takamaka-code-1.0.0.jar");

        Path familyPath = Paths.get("modules/family_exported-0.0.1-SNAPSHOT.jar");

        try (Node node = TendermintBlockchain.init(config, consensus)) {
            // 1) creo manifest e gamete
            InitializedNode initialized = InitializedNode.of
                    (node, consensus, takamakaCodePath, GREEN_AMOUNT, RED_AMOUNT);

            // 2) the gamete andrà a pagare per la transazione con unità di "gas"
            NodeWithJars nodeWithJars = NodeWithJars.of
                    (node, initialized.gamete(), initialized.keysOfGamete().getPrivate(),
                            familyPath);

            // 3) Assegno delle "monete" agli account per poter pagare le transazioni
            NodeWithAccounts nodeWithAccounts = NodeWithAccounts.of
                    (node, initialized.gamete(), initialized.keysOfGamete().getPrivate(),
                            BigInteger.valueOf(10_000_000), BigInteger.valueOf(20_000_000));

            GasHelper gasHelper = new GasHelper(node);

            // mittente: carico in blockchain
            StorageReference personStorage = node.addConstructorCallTransaction
                    (new ConstructorCallTransactionRequest(

                            // request da parte del primo account
                            SignedTransactionRequest.Signer.with(node.getSignatureAlgorithmForRequests(),
                                    nodeWithAccounts.privateKey(0)),

                            // il primo account paga la request
                            nodeWithAccounts.account(0),

                            ZERO,

                            "",

                            // gas usato per pagare la transazione
                            BigInteger.valueOf(10_000),

                            // il costo del gas per transazione
                            panarea(gasHelper.getSafeGasPrice()),

                            nodeWithJars.jar(0),

                            // Person(String,int,int,int)
                            new ConstructorSignature(PERSON, ClassType.STRING, INT, INT, INT),

                            // parametri attuali
                            new StringValue(person.getName()), new IntValue(person.getDay()),
                            new IntValue(person.getMonth()), new IntValue(person.getYear())
                    ));

            StorageValue s = node.addInstanceMethodCallTransaction(new InstanceMethodCallTransactionRequest(

                    // request da parte del secondo account per ottenere una response dalla blockchain
                    SignedTransactionRequest.Signer.with(node.getSignatureAlgorithmForRequests(), nodeWithAccounts.privateKey(1)),

                    // il primo account paga la response
                    nodeWithAccounts.account(1),

                    ZERO,

                    "",

                    // gas usato per pagare la transazione
                    BigInteger.valueOf(10_000),

                    // il costo del gas per transazione
                    panarea(gasHelper.getSafeGasPrice()),

                    nodeWithJars.jar(0),

                    // chiamo il toString()
                    new NonVoidMethodSignature(PERSON, "toString", ClassType.STRING),

                    // ripongo la response in uno storage per usi futuri
                    personStorage
            ));

            result = "Operazione terminata con successo. Account: " + s.toString() + " creato correttamente";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
