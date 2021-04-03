package com.hotmokafe.application.blockchain;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.hotmokafe.application.utils.Kernel;
import io.hotmoka.beans.CodeExecutionException;
import io.hotmoka.beans.TransactionException;
import io.hotmoka.beans.TransactionRejectedException;
import io.hotmoka.beans.references.TransactionReference;
import io.hotmoka.beans.updates.ClassTag;
import io.hotmoka.beans.updates.Update;
import io.hotmoka.beans.updates.UpdateOfField;
import io.hotmoka.beans.updates.UpdateOfString;
import io.hotmoka.beans.values.StorageReference;
import io.hotmoka.nodes.Node;
import io.takamaka.code.constants.Constants;
import io.takamaka.code.verification.TakamakaClassLoader;
import io.takamaka.code.whitelisting.WhiteListingWizard;

import static com.hotmokafe.application.blockchain.AbstractCommand.*;

class PrintAPI {
    private final Class<?> clazz;
    private final WhiteListingWizard whiteListingWizard;
    private final Update[] updates;
    private final ClassTag tag;

    //output
    private final List<String> output = new ArrayList<>();

    public List<String> getOutput() {
        return output;
    }

    PrintAPI(Node node) throws ClassNotFoundException, TransactionRejectedException, TransactionException, CodeExecutionException {
        this.updates = node.getState(new StorageReference(Kernel.getInstance().getAccountLogged().getReference())).sorted().toArray(Update[]::new);
        this.tag = getClassTag();
        TakamakaClassLoader classloader = new ClassLoaderHelper(node).classloaderFor(tag.jar);
        this.clazz = classloader.loadClass(tag.clazz.name);
        this.whiteListingWizard = classloader.getWhiteListingWizard();

        print();
    }

    private void print() throws ClassNotFoundException {
        printFieldsInClass();
        printFieldsInherited();
        printConstructors();
        printMethods();
        Kernel.getInstance().getAccountLogged().setInnerClass(output);
    }

    private void printFieldsInherited() {
        output.add("Fields inherited");

        Stream.of(updates)
                .filter(update -> update instanceof UpdateOfField)
                .map(update -> (UpdateOfField) update)
                .filter(update -> !update.field.definingClass.equals(tag.clazz))
                .forEachOrdered(this::printUpdate);
    }

    private void printFieldsInClass() {
        output.add("Fields");

        Stream.of(updates)
                .filter(update -> update instanceof UpdateOfField)
                .map(update -> (UpdateOfField) update)
                .filter(update -> update.field.definingClass.equals(this.tag.clazz))
                .forEachOrdered(this::printUpdate);
    }

    private void printUpdate(UpdateOfField update) {
        if (tag.clazz.equals(update.field.definingClass))
            output.add(ANSI_RESET + "  " + update.field.name + ":" + update.field.type + " = " + valueToPrint(update));
        else
            output.add(ANSI_CYAN + "\u25b2 " + update.field.name + ":" + update.field.type + " = " + valueToPrint(update) + ANSI_GREEN + " (inherited from " + update.field.definingClass + ")");
    }

    private String valueToPrint(UpdateOfField update) {
        if (update instanceof UpdateOfString)
            return '\"' + update.getValue().toString() + '\"';
        else
            return update.getValue().toString();
    }

    private ClassTag getClassTag() {
        return Stream.of(updates)
                .filter(update -> update instanceof ClassTag)
                .map(update -> (ClassTag) update)
                .findFirst().get();
    }

    private void printMethods() throws ClassNotFoundException {
        Comparator<Method> comparator = Comparator.comparing(Method::getName)
                .thenComparing(Method::toString);

        output.add("Methods");

        Method[] methods = clazz.getMethods();
        List<Method> defined = Stream.of(methods)
                .sorted(comparator)
                .filter(method -> method.getDeclaringClass() == clazz)
                .collect(Collectors.toList());

        for (Method method: defined)
            printMethod(method);

        List<Method> inherited = Stream.of(methods)
                .sorted(comparator)
                .filter(method -> method.getDeclaringClass() != clazz)
                .collect(Collectors.toList());

        output.add("Inherited methods");

        for (Method method: inherited)
            printInheritedMethod(method);
    }

    private void printConstructors() throws ClassNotFoundException {
        Constructor<?>[] constructors = clazz.getConstructors();

        output.add("Contructors");

        for (Constructor<?> constructor: constructors)
            printConstructor(constructor);
    }

    private void printConstructor(Constructor<?> constructor) throws ClassNotFoundException {
        Class<?> clazz = constructor.getDeclaringClass();
        output.add(ANSI_RESET + "  "
                + annotationsAsString(constructor)
                + constructor.toString().replace(clazz.getName() + "(", clazz.getSimpleName() + "(")
                + (whiteListingWizard.whiteListingModelOf(constructor).isEmpty() ? (AbstractCommand.ANSI_RED + " \u274c") : ""));
    }

    private String annotationsAsString(Executable executable) {
        String prefix = Constants.IO_TAKAMAKA_CODE_LANG_PACKAGE_NAME;
        String result = Stream.of(executable.getAnnotations())
                .filter(annotation -> annotation.annotationType().getName().startsWith(prefix))
                .map(Annotation::toString)
                .collect(Collectors.joining(" "))
                .replace(prefix, "")
                .replace("()", "")
                .replace("(Contract.class)", "");

        if (result.isEmpty())
            return "";
        else
            return AbstractCommand.ANSI_RED + result + ANSI_RESET + ' ';
    }

    private void printMethod(Method method) throws ClassNotFoundException {
        output.add(ANSI_RESET + "  "
                + annotationsAsString(method)
                + method.toString().replace(method.getDeclaringClass().getName() + "." + method.getName(), method.getName())
                + (whiteListingWizard.whiteListingModelOf(method).isEmpty() ? (AbstractCommand.ANSI_RED + " \u274c") : ""));
    }

    private void printInheritedMethod(Method method) throws ClassNotFoundException {
        Class<?> definingClass = method.getDeclaringClass();
        output.add(ANSI_CYAN + "\u25b2 "
                + annotationsAsString(method) + ANSI_CYAN
                + method.toString().replace(method.getDeclaringClass().getName() + "." + method.getName(), method.getName())
                + ANSI_GREEN + " (inherited from " + definingClass.getName() + ")"
                + (whiteListingWizard.whiteListingModelOf(method).isEmpty() ? (AbstractCommand.ANSI_RED + " \u274c") : ""));
    }
}
