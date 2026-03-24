package com.pao.laboratory05.playlist;

import java.util.Arrays;

public class Playlist {
    private String name;
    private Song[] songs;

    public Playlist(String name) {
        this.name = name;
        this.songs = new Song[0];
    }

    public String getName() {
        return name;
    }

    public void addSong(Song song) {
        Song[] copie = new Song[songs.length + 1];
        System.arraycopy(songs, 0, copie, 0, songs.length);
        copie[copie.length - 1] = song;
        songs = copie;
    }

    public void printSortedByTitle() {
        Song[] copy = songs.clone();
        Arrays.sort(copy);

        for (Song song : copy) {
            System.out.println(song);
        }
    }

    public void printSortedByDuration() {
        Song[] copy = songs.clone();
        Arrays.sort(copy, new SongDurationComparator());

        for (Song song : copy) {
            System.out.println(song);
        }
    }

    public int getTotalDuration() {
        int total = 0;

        for (Song song : songs) {
            total += song.durationSeconds();
        }

        return total;
    }
}