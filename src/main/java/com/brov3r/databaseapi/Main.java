package com.brov3r.databaseapi;

import com.avrix.plugin.Metadata;
import com.avrix.plugin.Plugin;
import com.avrix.plugin.ServiceManager;
import com.brov3r.databaseapi.services.DatabaseAPI;
import com.brov3r.databaseapi.services.DatabaseAPIImpl;

/**
 * Main entry point
 */
public class Main extends Plugin {
    /**
     * Constructs a new {@link Plugin} with the specified metadata.
     * Metadata is transferred when the plugin is loaded into the game context.
     *
     * @param metadata The {@link Metadata} associated with this plugin.
     */
    public Main(Metadata metadata) {
        super(metadata);
    }

    /**
     * Called when the plugin is initialized.
     * <p>
     * Implementing classes should override this method to provide the initialization logic.
     */
    @Override
    public void onInitialize() {
        ServiceManager.register(DatabaseAPI.class, new DatabaseAPIImpl());
    }
}