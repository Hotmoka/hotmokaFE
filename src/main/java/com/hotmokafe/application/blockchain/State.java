package com.hotmokafe.application.blockchain;

import java.util.stream.Stream;

import com.hotmokafe.application.utils.Kernel;
import io.hotmoka.beans.CodeExecutionException;
import io.hotmoka.beans.TransactionException;
import io.hotmoka.beans.TransactionRejectedException;
import io.hotmoka.nodes.Node;
import io.hotmoka.remote.RemoteNode;

public class State extends AbstractCommand {
    @Override
    protected void execute() throws Exception {
        new Run();
    }

    private class Run {
        private final Node node;

        private Run() throws Exception {
            try (Node ignored = this.node = RemoteNode.of(remoteNodeConfig(Kernel.getInstance().getUrl()))) {
                printAPI();
            }
        }

        private void printAPI() throws ClassNotFoundException, TransactionRejectedException, TransactionException, CodeExecutionException {
            new PrintAPI(node);
        }

    }
}