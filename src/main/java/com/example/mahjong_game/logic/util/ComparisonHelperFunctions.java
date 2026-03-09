package com.example.mahjong_game.logic.util;

import com.example.mahjong_game.model.tiles.FlowerTile;
import com.example.mahjong_game.model.tiles.HonorTile;
import com.example.mahjong_game.model.tiles.SuitedTile;
import com.example.mahjong_game.model.tiles.Tile;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class ComparisonHelperFunctions {

    public Comparator<Tile> getTileLogicComparator() {
        return Comparator.comparing((Tile t) -> t.getClass().getSimpleName())
                .thenComparing(Tile::getSuit, Comparator.nullsLast(String::compareTo))
                .thenComparing(Tile::getNumber, Comparator.nullsLast(Integer::compareTo));
    }

    public Map<String, List<Tile>> groupTilesBySuit(List<Tile> tiles) {
        return tiles.stream().collect(Collectors.groupingBy(Tile::getSuit)); // e.g <bamboo: [t1, t2, t3]>
    }

    public Map<String, List<Tile>> groupTilesBySuitAndNumber(List<Tile> tiles) {
        return tiles.stream().collect(Collectors.groupingBy(t -> t.getSuit() + "-" + t.getNumber())); // e.g <bamboo-3: [t1, t2, t3]>
    }

    public List<Tile> getOnlyHonorSuitedOrFlowerTiles(List<Tile> tiles, String honorSuitedOrFlower) {
        return switch (honorSuitedOrFlower) {
            case "Honor" -> tiles.stream().filter(HonorTile.class::isInstance).toList();
            case "Suited" -> tiles.stream().filter(SuitedTile.class::isInstance).toList();
            case "Flower" -> tiles.stream().filter(FlowerTile.class::isInstance).toList();
            default -> null;
        };
    }

    public boolean sameTypeNumberAndSuitCheck(Tile tile1, Tile tile2) {
        if (!sameTypeAndSuitCheck(tile1, tile2)) return false;
        return Objects.equals(tile1.getNumber(), tile2.getNumber());
    }

    public boolean sameTypeAndSuitCheck(Tile tile1, Tile tile2) {
        if (!sameTypeCheck(tile1, tile2)) return false;
        return Objects.equals(tile1.getSuit(), tile2.getSuit());
    }

    public boolean sameTypeCheck(Tile tile1, Tile tile2) {
        if (!notNullCheck(tile1, tile2)) return false;
        return tile1.getClass() == tile2.getClass();
    }

    public boolean notNullCheck(Tile tile1, Tile tile2) {
        return (tile1 != null && tile2 != null);
    }

}
