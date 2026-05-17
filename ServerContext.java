package com.wordy.server;

import com.wordy.server.controller.AdminController;
import com.wordy.server.controller.GameController;
import com.wordy.server.controller.LeaderboardController;
import com.wordy.server.controller.LoginController;
import com.wordy.server.model.game.GameLobby;
import com.wordy.server.model.game.LetterGenerator;
import com.wordy.server.model.game.WordDictionary;
import com.wordy.server.model.session.SessionRegistry;
import com.wordy.server.service.AdminService;
import com.wordy.server.service.AuthService;
import com.wordy.server.service.GameService;
import com.wordy.server.service.LeaderboardService;

import java.io.IOException;

public final class ServerContext {

    private final LoginController loginController;
    private final AdminController adminController;
    private final GameController gameController;
    private final LeaderboardController leaderboardController;

    private ServerContext(
            LoginController loginController,
            AdminController adminController,
            GameController gameController,
            LeaderboardController leaderboardController
    ) {
        this.loginController = loginController;
        this.adminController = adminController;
        this.gameController = gameController;
        this.leaderboardController = leaderboardController;
    }

    public static ServerContext createDefault() throws IOException {
        RepositoryProvider.Repositories repositories = RepositoryProvider.create();
        SessionRegistry sessionRegistry = SessionRegistry.getInstance();

        AuthService authService = new AuthService(repositories.players(), sessionRegistry);

        GameLobby gameLobby = new GameLobby(
                WordDictionary.loadDefault(),
                new LetterGenerator(),
                repositories.config(),
                sessionRegistry,
                repositories.players(),
                repositories.leaderboard()
        );
        AdminService adminService = new AdminService(
                repositories.players(),
                repositories.config(),
                sessionRegistry,
                gameLobby
        );
        LeaderboardService leaderboardService = new LeaderboardService(
                repositories.players(),
                repositories.leaderboard()
        );
        GameService gameService = new GameService(sessionRegistry, gameLobby);

        return new ServerContext(
                new LoginController(authService),
                new AdminController(adminService),
                new GameController(gameService),
                new LeaderboardController(leaderboardService)
        );
    }

    public LoginController loginController() {
        return loginController;
    }

    public AdminController adminController() {
        return adminController;
    }

    public GameController gameController() {
        return gameController;
    }

    public LeaderboardController leaderboardController() {
        return leaderboardController;
    }
}
