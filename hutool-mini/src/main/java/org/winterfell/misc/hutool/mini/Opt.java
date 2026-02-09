package org.winterfell.misc.hutool.mini;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/5/20
 */
public class Opt<T> {

    /**
     * 一个空的{@code Opt}
     */
    private static final Opt<?> EMPTY = new Opt<>(null);

    /**
     * 包裹里实际的元素
     */
    private final T value;
    private Exception exception;

    /**
     * {@code Opt}的构造函数
     *
     * @param value 包裹里的元素
     */
    private Opt(T value) {
        this.value = value;
    }


    /**
     * 返回包裹里的元素，取不到则为{@code null}，注意！！！此处和{@link java.util.Optional#get()}不同的一点是本方法并不会抛出{@code NoSuchElementException}
     * 如果元素为空，则返回{@code null}，如果需要一个绝对不能为{@code null}的值，则使用{@link #orElseThrow()}
     *
     * <p>
     * 如果需要一个绝对不能为 {@code null}的值，则使用{@link #orElseThrow()}
     * 做此处修改的原因是，有时候我们确实需要返回一个null给前端，并且这样的时候并不少见
     * 而使用 {@code .orElse(null)}需要写整整12个字符，用{@code .get()}就只需要6个啦
     *
     * @return 包裹里的元素，有可能为{@code null}
     */
    public T get() {
        return this.value;
    }

    /**
     * 判断包裹里元素的值是否不存在，不存在为 {@code true}，否则为{@code false}
     *
     * @return 包裹里元素的值不存在 则为 {@code true}，否则为{@code false}
     * @since 11 这是jdk11{@link java.util.Optional}中的新函数
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * 获取异常<br>
     * 当调用 {@link #ofTry(Func0)}时，异常信息不会抛出，而是保存，调用此方法获取抛出的异常
     *
     * @return 异常
     * @since 5.7.17
     */
    public Exception getException() {
        return this.exception;
    }

    /**
     * 是否失败<br>
     * 当调用 {@link #ofTry(Func0)}时，抛出异常则表示失败
     *
     * @return 是否失败
     * @since 5.7.17
     */
    public boolean isFail() {
        return null != this.exception;
    }

    /**
     * 如果包裹内容失败了，则执行传入的操作({@link Consumer#accept})
     *
     * <p> 例如执行有异常就打印结果
     * <pre>{@code
     *     Opt.ofTry(() -> 1 / 0).ifFail(Console::log);
     * }</pre>
     *
     * @param action 你想要执行的操作
     * @return this
     * @throws NullPointerException 如果包裹里的值存在，但你传入的操作为{@code null}时抛出
     */
    public Opt<T> ifFail(final Consumer<? super Throwable> action) throws NullPointerException {
        Objects.requireNonNull(action, "action is null");

        if (isFail()) {
            action.accept(this.exception);
        }

        return this;
    }

    /**
     * 判断包裹里元素的值是否存在，存在为 {@code true}，否则为{@code false}
     *
     * @return 包裹里元素的值存在为 {@code true}，否则为{@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * 如果包裹里的值存在，就执行传入的操作({@link Consumer#accept})
     *
     * <p> 例如如果值存在就打印结果
     * <pre>{@code
     * Opt.ofNullable("Hello Hutool!").ifPresent(Console::log);
     * }</pre>
     *
     * @param action 你想要执行的操作
     * @return this
     * @throws NullPointerException 如果包裹里的值存在，但你传入的操作为{@code null}时抛出
     */
    public Opt<T> ifPresent(Consumer<? super T> action) {
        if (isPresent()) {
            action.accept(value);
        }
        return this;
    }


    /**
     * @param supplier 操作
     * @param <T>      类型
     * @return 操作执行后的值
     */
    public static <T> Opt<T> ofTry(Func0<T> supplier) {
        try {
            return Opt.ofNullable(supplier.call());
        } catch (Exception e) {
            final Opt<T> empty = new Opt<>(null);
            empty.exception = e;
            return empty;
        }
    }

    /**
     * 如果包裹里元素的值存在，则返回该值，否则返回传入的{@code other}
     *
     * @param other 元素为空时返回的值，有可能为 {@code null}.
     * @return 如果包裹里元素的值存在，则返回该值，否则返回传入的{@code other}
     */
    public T orElse(T other) {
        return isPresent() ? value : other;
    }

    /**
     * 异常则返回另一个可选值
     *
     * @param other 可选值
     * @return 如果未发生异常，则返回该值，否则返回传入的{@code other}
     * @since 5.7.17
     */
    public T exceptionOrElse(T other) {
        return isFail() ? other : value;
    }

    /**
     * 如果包裹里元素的值存在，则返回该值，否则返回传入的操作执行后的返回值
     *
     * @param supplier 值不存在时需要执行的操作，返回一个类型与 包裹里元素类型 相同的元素
     * @return 如果包裹里元素的值存在，则返回该值，否则返回传入的操作执行后的返回值
     * @throws NullPointerException 如果之不存在，并且传入的操作为空，则抛出 {@code NPE}
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return isPresent() ? value : supplier.get();
    }

    /**
     * 如果包裹里的值存在，则返回该值，否则抛出 {@code NoSuchElementException}
     *
     * @return 返回一个不为 {@code null} 的包裹里的值
     * @throws NoSuchElementException 如果包裹里的值不存在则抛出该异常
     */
    public T orElseThrow() {
        return orElseThrow(NoSuchElementException::new, "No value present");
    }

    /**
     * 如果包裹里的值存在，则返回该值，否则执行传入的操作，获取异常类型的返回值并抛出
     * <p>往往是一个包含无参构造器的异常 例如传入{@code IllegalStateException::new}
     *
     * @param <X>               异常类型
     * @param exceptionSupplier 值不存在时执行的操作，返回值继承 {@link Throwable}
     * @return 包裹里不能为空的值
     * @throws X                    如果值不存在
     * @throws NullPointerException 如果值不存在并且 传入的操作为 {@code null}或者操作执行后的返回值为{@code null}
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 如果包裹里的值存在，则返回该值，否则执行传入的操作，获取异常类型的返回值并抛出
     *
     * <p>往往是一个包含 自定义消息 构造器的异常 例如
     * <pre>{@code
     * 		Opt.ofNullable(null).orElseThrow(IllegalStateException::new, "Ops!Something is wrong!");
     * }</pre>
     *
     * @param <X>               异常类型
     * @param exceptionFunction 值不存在时执行的操作，返回值继承 {@link Throwable}
     * @param message           作为传入操作执行时的参数，一般作为异常自定义提示语
     * @return 包裹里不能为空的值
     * @throws X                    如果值不存在
     * @throws NullPointerException 如果值不存在并且 传入的操作为 {@code null}或者操作执行后的返回值为{@code null}
     * @author VampireAchao
     */
    public <X extends Throwable> T orElseThrow(Function<String, ? extends X> exceptionFunction, String message) throws X {
        if (isPresent()) {
            return value;
        } else {
            throw exceptionFunction.apply(message);
        }
    }

    /**
     * 转换为 {@link Optional}对象
     *
     * @return {@link Optional}对象
     * @since 5.7.16
     */
    public Optional<T> toOptional() {
        return Optional.ofNullable(this.value);
    }

    /**
     * 返回一个包裹里元素可能为空的{@code Opt}
     *
     * @param value 传入需要包裹的元素
     * @param <T>   包裹里元素的类型
     * @return 一个包裹里元素可能为空的 {@code Opt}
     */
    public static <T> Opt<T> ofNullable(T value) {
        return value == null ? empty()
                : new Opt<>(value);
    }

    /**
     * 如果包裹里的值存在，就执行传入的操作({@link Function#apply})并返回一个包裹了该操作返回值的{@code Opt}
     * 如果不存在，返回一个空的{@code Opt}
     *
     * @param mapper 值存在时执行的操作
     * @param <U>    操作返回值的类型
     * @return 如果包裹里的值存在，就执行传入的操作({@link Function#apply})并返回一个包裹了该操作返回值的{@code Opt}，
     * 如果不存在，返回一个空的{@code Opt}
     * @throws NullPointerException 如果给定的操作为 {@code null}，抛出 {@code NPE}
     */
    public <U> Opt<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            return Opt.ofNullable(mapper.apply(value));
        }
    }

    /**
     * 如果包裹里的值存在，就执行传入的操作({@link Function#apply})并返回该操作返回值
     * 如果不存在，返回一个空的{@code Opt}
     * 和 {@link Opt#map}的区别为 传入的操作返回值必须为 Opt
     *
     * @param mapper 值存在时执行的操作
     * @param <U>    操作返回值的类型
     * @return 如果包裹里的值存在，就执行传入的操作({@link Function#apply})并返回该操作返回值
     * 如果不存在，返回一个空的{@code Opt}
     * @throws NullPointerException 如果给定的操作为 {@code null}或者给定的操作执行结果为 {@code null}，抛出 {@code NPE}
     */
    public <U> Opt<U> flatMap(Function<? super T, ? extends Opt<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            final Opt<U> r = (Opt<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    /**
     * 如果包裹里的值存在，就执行传入的操作({@link Function#apply})并返回该操作返回值
     * 如果不存在，返回一个空的{@code Opt}
     * 和 {@link Opt#map}的区别为 传入的操作返回值必须为 {@link Optional}
     *
     * @param mapper 值存在时执行的操作
     * @param <U>    操作返回值的类型
     * @return 如果包裹里的值存在，就执行传入的操作({@link Function#apply})并返回该操作返回值
     * 如果不存在，返回一个空的{@code Opt}
     * @throws NullPointerException 如果给定的操作为 {@code null}或者给定的操作执行结果为 {@code null}，抛出 {@code NPE}
     * @see Optional#flatMap(Function)
     * @since 5.7.16
     */
    public <U> Opt<U> flattedMap(Function<? super T, ? extends Optional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            return ofNullable(mapper.apply(value).orElse(null));
        }
    }


    /**
     * 如果包裹里元素的值存在，就执行对应的操作，并返回本身
     * 如果不存在，返回一个空的{@code Opt}
     *
     * <p>属于 {@link #ifPresent}的链式拓展
     *
     * @param action 值存在时执行的操作
     * @return this
     * @throws NullPointerException 如果值存在，并且传入的操作为 {@code null}
     * @author VampireAchao
     */
    public Opt<T> peek(Consumer<T> action) throws NullPointerException {
        Objects.requireNonNull(action);
        if (isEmpty()) {
            return Opt.empty();
        }
        action.accept(value);
        return this;
    }


    /**
     * 如果包裹里元素的值存在，就执行对应的操作集，并返回本身
     * 如果不存在，返回一个空的{@code Opt}
     *
     * <p>属于 {@link #ifPresent}的链式拓展
     * <p>属于 {@link #peek(Consumer)}的动态拓展
     *
     * @param actions 值存在时执行的操作，动态参数，可传入数组，当数组为一个空数组时并不会抛出 {@code NPE}
     * @return this
     * @throws NullPointerException 如果值存在，并且传入的操作集中的元素为 {@code null}
     * @author VampireAchao
     */
    @SafeVarargs
    public final Opt<T> peeks(Consumer<T>... actions) throws NullPointerException {
        // 第三个参数 (opts, opt) -> null其实并不会执行到该函数式接口所以直接返回了个null
        return Stream.of(actions).reduce(this, Opt<T>::peek, (opts, opt) -> null);
    }

    /**
     * 返回一个空的{@code Opt}
     *
     * @param <T> 包裹里元素的类型
     * @return Opt
     */
    @SuppressWarnings("unchecked")
    public static <T> Opt<T> empty() {
        return (Opt<T>) EMPTY;
    }

}
