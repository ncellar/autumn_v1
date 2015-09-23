package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.Extension;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.util.Array;

import java.util.function.Supplier;

/**
 * Builder pattern for {@link ParserConfiguration}.
 */
public final class ParserConfigurationBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Supplier<? extends ErrorHandler> errorHandler;
    private Supplier<? extends MemoHandler> memoHandler;

    private Array<Supplier<? extends Object>> scoped;
    private Array<Supplier<? extends CustomState>> customStates;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParserConfigurationBuilder errorHandler(ErrorHandler handler)
    {
        this.errorHandler = () -> handler;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public ParserConfigurationBuilder errorHandler(Supplier<? extends ErrorHandler> handlerSupplier)
    {
        this.errorHandler = handlerSupplier;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public ParserConfigurationBuilder memoStrategy(Supplier<? extends MemoHandler> handlerSupplier)
    {
        this.memoHandler = handlerSupplier;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Registers an extension with the configuration. The {@link Extension#register} method will
     * be called, allowing the extension to further modify the configuration. In particular, it
     * can register scoped objects and custom states; and should take care to save the returned
     * indices, so that they can be used by the parsing expressions of this extension.
     */
    public ParserConfigurationBuilder extension(Extension extension)
    {
        extension.register(this);
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Registers a scoped object with the configuration. Returns the scoped index.
     * This method should only be called from {@link Extension#register}.
     */
    public int scoped(Supplier<? extends Object> supplier)
    {
        if (scoped == null) scoped = new Array<>();
        scoped.add(supplier);
        return scoped.size() - 1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Registers a custom state with the configuration. Returns the custom state index.
     * This method should only be called from {@link Extension#register}.
     */
    public int customState(Supplier<? extends CustomState> supplier)
    {
        if (customStates == null) customStates = new Array<>();
        customStates.add(supplier);
        return customStates.size() - 1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParserConfiguration build()
    {
        return new ParserConfiguration()
        {
            @Override
            public ErrorHandler errorHandler()
            {
                return errorHandler != null
                    ? errorHandler.get()
                    : new DefaultErrorHandler();
            }

            @Override
            public MemoHandler memoHandler()
            {
                return memoHandler != null
                    ? memoHandler.get()
                    : new DefaultMemoHandler();
            }

            @Override
            public Object[] scoped()
            {
                if (scoped == null)
                {
                    return new Object[0];
                }

                return scoped.mapToArray(x -> x.get());
            }

            @Override
            public CustomState[] customStates()
            {
                if (customStates == null)
                {
                    return new CustomState[0];
                }

                return customStates.mapToArray(x -> x.get(), CustomState[]::new);
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
