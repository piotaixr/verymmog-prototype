/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.verymmog.demo;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.verymmog.demo.model.Player;
import com.verymmog.model.PlayerInterface;
import com.verymmog.model.map.MapInterface;
import com.verymmog.network.DataPlayer;
import com.verymmog.network.message.messages.ClientDataUpdateMessage;
import com.verymmog.network.player.ClientDataUpdaterInterface;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author marion
 */
public class Data<MapClass extends MapInterface> implements ClientDataUpdaterInterface {

    private Logger logger = Logger.getLogger(getClass());
    private MapClass map;
    private PlayerInterface player;
    private LoadingCache<String, PlayerInterface> players;
    private List<PlayerInterface> podium = new ArrayList<>();
    private long numberPeopleBefore;
    private long numberPeopleInMyLevel;

    public Data(MapClass map, PlayerInterface player) {
        this(map, player, new ArrayList<PlayerInterface>(), new ArrayList<PlayerInterface>());
    }

    public Data(MapClass map,
                PlayerInterface player,
                List<PlayerInterface> neighbours,
                List<PlayerInterface> podium) {
        this.map = map;
        this.player = player;
        players = CacheBuilder.newBuilder()
                              .expireAfterAccess(2L, TimeUnit.SECONDS)
                              .build(CacheLoader.from(new Function<String, PlayerInterface>() {
                                  @Override
                                  public PlayerInterface apply(String s) {
                                      return new Player(0, 0, s);
                                  }
                              }));
    }

    public MapClass getMap() {
        return map;
    }

    public PlayerInterface getPlayer() {
        return player;
    }

    public Collection<PlayerInterface> getPlayers() {
        return players.asMap().values();
    }

    public List<PlayerInterface> getPodium() {
        return podium;
    }

    public void setPodium(List<PlayerInterface> podium) {
        this.podium = podium;
    }

    public long getNumberPeopleBefore() {
        return numberPeopleBefore;
    }

    public void setNumberPeopleBefore(long numberPeopleBefore) {
        this.numberPeopleBefore = numberPeopleBefore;
    }

    public long getNumberPeopleInMyLevel() {
        return numberPeopleInMyLevel;
    }

    public void setNumberPeopleInMyLevel(long numberPeopleAfter) {
        this.numberPeopleInMyLevel = numberPeopleAfter;
    }

    @Override
    public void updatePlayers(ClientDataUpdateMessage message) {
        for (DataPlayer dpl : message.getPlayers()) {
            if (!dpl.name.equals(player.getName())) {
                players.getUnchecked(dpl.name).updatePosition(dpl.x, dpl.y).setSpeedVector(dpl.dx, dpl.dy);
            }
        }

        logger.trace("Fin update: " + players.size());
    }
}
