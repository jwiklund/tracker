#!/bin/sh
wget http://anidb.net/api/anime-titles.dat.gz
mv anime-titles.dat.gz anime-titles.dat.gz.bak
mv anime-titles.dat.gz.1 anime-titles.dat.gz
