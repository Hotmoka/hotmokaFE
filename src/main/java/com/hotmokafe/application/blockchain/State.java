package com.hotmokafe.application.blockchain;

import java.util.stream.Stream;

import io.hotmoka.beans.CodeExecutionException;
import io.hotmoka.beans.TransactionException;
import io.hotmoka.beans.TransactionRejectedException;
import io.hotmoka.beans.types.ClassType;
import io.hotmoka.beans.updates.ClassTag;
import io.hotmoka.beans.updates.Update;
import io.hotmoka.beans.updates.UpdateOfField;
import io.hotmoka.beans.updates.UpdateOfString;
import io.hotmoka.beans.values.StorageReference;
import io.hotmoka.nodes.Node;
import io.hotmoka.remote.RemoteNode;

public class State extends AbstractCommand {

    //"the reference to the object"
    private String object;

    //"the url of the node (without the protocol)"
    private String url;

    //"prints the public API of the object"
    private boolean api;

    @Override
    protected void execute() throws Exception {
        new Run();
    }

    private class Run {
        private final Node node;
        private final Update[] updates;
        private final ClassTag tag;

        private Run() throws Exception {
            StorageReference reference = new StorageReference(object);

            try (Node node = this.node = RemoteNode.of(remoteNodeConfig(url))) {
                this.updates = node.getState(reference).sorted().toArray(Update[]::new);
                this.tag = getClassTag();

                printHeader();
                printFieldsInClass();
                printFieldsInherited();
                printAPI();
            }
        }

        private void printAPI() throws ClassNotFoundException, TransactionRejectedException, TransactionException, CodeExecutionException {
            System.out.println();
            if (api)
                new PrintAPI(node, tag.jar, tag.clazz.name);
        }

        private void printFieldsInherited() {
            Stream.of(updates)
                    .filter(update -> update instanceof UpdateOfField)
                    .map(update -> (UpdateOfField) update)
                    .filter(update -> !update.field.definingClass.equals(tag.clazz))
                    .forEachOrdered(this::printUpdate);
        }

        private void printFieldsInClass() {
            Stream.of(updates)
                    .filter(update -> update instanceof UpdateOfField)
                    .map(update -> (UpdateOfField) update)
                    .filter(update -> update.field.definingClass.equals(tag.clazz))
                    .forEachOrdered(this::printUpdate);
        }

        private void printHeader() {
            ClassType clazz = tag.clazz;
            System.out.println(ANSI_RED + "\nThis is the state of object " + object + "@" + url + "\n");
            System.out.println(ANSI_RESET + "class " + clazz + " (from jar installed at " + tag.jar + ")");
        }

        private ClassTag getClassTag() {
            return Stream.of(updates)
                    .filter(update -> update instanceof ClassTag)
                    .map(update -> (ClassTag) update)
                    .findFirst().get();
        }

        private void printUpdate(UpdateOfField update) {
            if (tag.clazz.equals(update.field.definingClass))
                System.out.println(ANSI_RESET + "  " + update.field.name + ":" + update.field.type + " = " + valueToPrint(update));
            else
                System.out.println(ANSI_CYAN + "\u25b2 " + update.field.name + ":" + update.field.type + " = " + valueToPrint(update) + ANSI_GREEN + " (inherited from " + update.field.definingClass + ")");
        }

        private String valueToPrint(UpdateOfField update) {
            if (update instanceof UpdateOfString)
                return '\"' + update.getValue().toString() + '\"';
            else
                return update.getValue().toString();
        }
    }
}