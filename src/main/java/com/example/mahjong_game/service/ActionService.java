package com.example.mahjong_game.service;

import com.example.mahjong_game.exception.ActionFailedException;
import com.example.mahjong_game.exception.GetFromDatabaseFailedException;
import com.example.mahjong_game.exception.TileCreationFailedException;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Action;
import com.example.mahjong_game.model.actions.Chow;
import com.example.mahjong_game.model.actions.Kong;
import com.example.mahjong_game.model.actions.Pung;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.repository.ActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionService {

    private final ActionRepository actionRepository; //The action repository for interacting with the database
    private static final Logger logger = LoggerFactory.getLogger(ActionService.class);
    public ActionService(ActionRepository actionRepository) { //Initialises the user Repository
        this.actionRepository = actionRepository;
    }
    
    public Action createAction(String type) {
        try {
            Action action = switch (type) {
                case "Chow" -> new Chow();
                case "Pung" -> new Pung();
                case "Kong" -> new Kong();
                default -> null;
            };
            if (action == null) throw new TileCreationFailedException("Invalid action type: " + type);
            action.setActionType(type);
            logger.info("Created action: {}", type);
            action = actionRepository.save(action);
            return action;
        } catch (Exception e) {
            throw new TileCreationFailedException("Failed to create action: ", e);
        }
    }

    public void deleteAction(Integer actionId) {
        try {
            actionRepository.delete(findActionById(actionId));
        } catch (Exception e) {
            throw new ActionFailedException("Failed to delete action with ID: " + actionId, e);
        }
    }

    public Action findActionById(Integer actionId) {
        try {
            return actionRepository.findById(actionId).orElseThrow(() -> new GetFromDatabaseFailedException("Action not found with Id: " + actionId));
        } catch (GetFromDatabaseFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to find action with ID: " + actionId, e);
        }
    }

    public Action addTileToAction(Integer actionId, Tile tile) {
        try {
            Action action = findActionById(actionId);
            action.addTile(tile);
            actionRepository.save(action);
            return action;
        } catch (Exception e) {
            throw new ActionFailedException("Failed to add tile to action", e);
        }
    }

    public void removeTileFromAction(Integer actionId, Tile tile) {
        try {
            Action action = findActionById(actionId);
            action.removeTile(tile);
            actionRepository.save(action);
        } catch (Exception e) {
            throw new ActionFailedException("Failed to remove tile from action", e);
        }
    }

    public boolean lookInPlayerForActionWithSameTiles(Player player, List<Tile> actionTiles, String type) {
        try {
            List<Action> existingActionsWithPlayer = findPlayersActionsByType(player, type);
            for (Action existingAction : existingActionsWithPlayer) {
                boolean matchFound = true;
                List<Tile> existingTiles = existingAction.getTiles();
                for (int i = 0; i < 3; i++) {
                    if (existingTiles.get(i).getTileId() != actionTiles.get(i).getTileId()) {
                        matchFound = false;
                        break; // This action does not match as any of the elements are diff
                    }
                }
                if (matchFound) {
                    return true; // Found duplicate action
                }
            }
            return false;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to look for duplicate actions for player: " + player.getUsername(), e);
        }
    }

    public Action saveAction(Integer actionId) {
        try {
            Action action = findActionById(actionId);
            action = actionRepository.save(action);
            return action;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to save an existing action", e);
        }
    }

    public List<Action> findPlayersActionsByType(Player player, String type) {
        List<Action> allActions = actionRepository.findAllByPlayer(player);
        return allActions.stream().filter(action -> action.getClass().getSimpleName().equals(type)).toList();
    }
}
