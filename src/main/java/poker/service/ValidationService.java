package poker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import poker.core.engine.GameEngine;
import poker.core.engine.GameEngineRegistry;
import poker.core.game.GameStatus;
import poker.core.game.GameTable;
import poker.core.player.GamePlayer;
import poker.dto.PlayerActionRequest;
import poker.dto.auth.LoginRequest;
import poker.dto.auth.RegistrationRequest;
import poker.dto.game.CreateGameRequest;
import poker.dto.game.StartGameRequest;
import poker.dto.profile.ProfileInfoRequest;
import poker.dto.profile.UpdatePasswordRequest;
import poker.model.Player;
import poker.model.PlayerDetails;
import poker.model.User;
import poker.util.Util;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidationService {
    private final PasswordEncoder passwordEncoder;
    private final GameEngineRegistry gameEngineRegistry;
    private final UserService userService;
    private final PlayerService playerService;

    public void validateRegistrationRequest(RegistrationRequest request) {
//        TODO: add email parsing validation

        if (userService.isUserExistsByEmail(request.email())) {
            log.info("Email {} already exists", request.email());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email " + request.email() + " taken");
        }

        if (playerService.isPlayerExistsByNickname(request.nickname())) {
            log.info("Nickname {} already exists", request.nickname());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname " + request.nickname() + " taken");
        }
    }

    public void validateLogin(User user, LoginRequest loginReq) {
        if (user == null) {
            log.info("User not found by email {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "User with email " + loginReq.email() + " not found");
        }

        if (!passwordEncoder.matches(loginReq.password(), user.getPassword())) {
            log.info("Passwords do not match for user {}", loginReq.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
    }

    public void validateUpdProfileInfo(ProfileInfoRequest request) {
        String nickname = request.nickname();
        if (playerService.isPlayerExistsByNickname(nickname)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nickname " + nickname + " taken");
        }
    }

    public void validateChangePassword(UpdatePasswordRequest request, PlayerDetails playerDetails) {
        String currentPassword = request.currentPassword();

        if (!passwordEncoder.matches(currentPassword, playerDetails.getUser().getPassword())) {
            log.info("Passwords do not match, user id {}", playerDetails.getUser().getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong current password");
        }

        if (currentPassword.equals(request.newPassword())) {
            log.info("The passwords must be different, user id {}", playerDetails.getUser().getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The new password must be different from the current one");
        }
    }

    public void validateCreatingGame(CreateGameRequest request) {
//        todo: add check for buyIn and maxPlayers values
        String gameName = request.name();
        for (GameEngine engine : gameEngineRegistry.getGameEngineCollection()) {
            if (engine.table().getName().equals(gameName)) {
                log.info("Game with name {} already exists", gameName);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Game with name " + gameName + " already exists");
            }
        }
    }

    public void validateGameDeletion(long gameId, PlayerDetails playerDetails) {
        GameTable table = gameEngineRegistry.getGameEngine(gameId).table();

        if (table.getCreatorPlayerId() != playerDetails.getUser().getId()) {
            log.error("Violation of authority to remove a game, user id {}", playerDetails.getUser().getId());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Only the creator of the game " + table.getName() + " can delete it");
        }

        if (!table.getGameStatus().equals(GameStatus.WAITING_FOR_PLAYERS)) {
            log.error("Attempting to delete game id {} in an inappropriate status, user id {}",
                gameId, playerDetails.getUser().getId());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "Game in '" + table.getGameStatus().getShortName() + "' status can not be deleted");
        }
    }

    public void validateStartGame(StartGameRequest request, PlayerDetails playerDetails) {
        long gameId = request.gameId();
        long userId = playerDetails.getUser().getId();
        GameTable table = gameEngineRegistry.getGameEngine(gameId).table();

        if (userId != table.getCreatorPlayerId()) {
            log.info("Attempting to start game id {} creator id {} user id {}",
                gameId, table.getCreatorPlayerId(), userId);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Only the creator of the game " + table.getName() + " can start it");
        }

        if (!table.getGameStatus().equals(GameStatus.WAITING_FOR_PLAYERS)) {
            log.error("Attempting to start game id {} in an inappropriate status, user id {}", gameId, userId);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "Game in '" + table.getGameStatus().getShortName() + "' status can not be started");
        }

        int currentPlayersAmount = table.getPlayers().size();
        if (currentPlayersAmount < Util.MIN_PLAYERS) {
            log.info("Not enough players to start the game. Current number of players: {}", currentPlayersAmount);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                "To start a game, at least " + Util.MIN_PLAYERS + " players must join. " +
                    "Current number of players: " + currentPlayersAmount);
        }
    }

    public boolean isGameExists(long gameId) {
        return gameEngineRegistry.getGameEngine(gameId) != null;
    }

    public boolean isPlayerActionValid(long gameId, PlayerDetails playerDetails, PlayerActionRequest request) {
        Player authPlayer = playerDetails.getPlayer();
        GameTable table = gameEngineRegistry.getGameEngine(gameId).table();
        GamePlayer player = table.getPlayerById(authPlayer.getId());

        if (player == null || !player.getNickname().equals(authPlayer.getNickname())) {
            log.error("Player id {} plays game id {} he is not sitting at", authPlayer.getId(), gameId);
            return false;
        }

        if (table.getActivePlayer().getId() != authPlayer.getId()
            || !table.getActivePlayer().getNickname().equals(authPlayer.getNickname())) {
            log.error("Player id {} makes a move in game id {} when it is not his turn", authPlayer.getId(), gameId);
            return false;
        }
        // todo: add checks:
        //      1. Is player action type correct?
        //      2. Is player has enough chips for that action?

        return true;
    }
}
