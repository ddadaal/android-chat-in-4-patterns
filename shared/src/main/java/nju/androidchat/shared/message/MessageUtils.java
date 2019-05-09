package nju.androidchat.shared.message;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtils {

    @SneakyThrows
    public void fillHandlerMap(Class<?> handlerClass, Map<Class<?>, Method> methodMap) {
        Arrays.stream(handlerClass.getMethods())
                .filter(x -> x.isAnnotationPresent(MessageHandler.class))
                .forEach(x -> {
                    methodMap.put(x.getParameters()[0].getType(), x);
                });
    }
}
