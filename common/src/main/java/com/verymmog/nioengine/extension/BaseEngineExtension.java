package com.verymmog.nioengine.extension;

import com.verymmog.nioengine.EngineInterface;
import com.verymmog.nioengine.event.EventDispatcherInterface;
import com.verymmog.nioengine.event.EventSubscriberInterface;
import com.verymmog.nioengine.extension.exception.ExtensionNotReadyException;

public class BaseEngineExtension implements EngineExtensionInterface, EventSubscriberInterface {
    private EngineInterface engine = null;
    private boolean running = false;

    @Override
    public EngineInterface getEngine() {
        return engine;
    }

    /**
     * Démarre l'extension. Appellée par l'engine:
     * - Si l'extension est ajoutée a une engine déja démarrée, boot() est appellée immédiatement.
     * - Si l'extension est ajoutée a une engine éteinte, boot est appellée lors du démarrage de l'engine.
     *
     * @throws ExtensionNotReadyException si l'engine n'a pas été renseignée.
     */
    @Override
    public final void boot() {
        if (engine == null) {
            throw ExtensionNotReadyException.engineIsNull();
        }

        doBoot();

        running = true;
    }

    /**
     * Procédure de boot, Peut être redéfinie pour ajouter un comportement personnalisé.
     */
    protected void doBoot() {
        register(engine.getEventDispatcher());
    }

    /**
     *
     */
    @Override
    public final void shutdown() {
        doShutdown();

        running = false;
    }

    /**
     *
     * @return true si l'extension est activée au sein d'une engine.
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * Procédure de shutdown, Peut être redéfinie pour ajouter un comportement personnalisé.
     */
    private void doShutdown() {
        unregister(engine.getEventDispatcher());
    }

    @Override
    public void setEngine(EngineInterface engine) {
        this.engine = engine;
    }

    /**
     * Methode appelee lors de la procédure de boot.
     * Enregistre les listeners auprès de l'eventDispatcher
     *
     * @param dispatcher
     */
    @Override
    public void register(EventDispatcherInterface dispatcher) {

    }

    /**
     * Désinstalle l'extension
     */
    protected void desinstall() {
        getEngine().removeExtension(this);
    }

    /**
     * Methode appelee lors de la procédure de shutdown.
     * Desinstalle tous les listeners enregistrés auprès de l'eventDispatcher de l'engine lors de l'execution de l'extension
     *
     * @param dispatcher
     */
    @Override
    public void unregister(EventDispatcherInterface dispatcher) {

    }
}
