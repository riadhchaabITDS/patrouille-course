# La Patrouille qui fait la course

Le jeu de Sofia : des chiots font 3 courses (moto, voiture, ski), ramassent des étoiles, évitent les maisons, les cabanes et les feux rouges, et gagnent des médailles et la coupe du monde. On peut jouer à 1 ou à 2 joueurs.

- `maquette.html` : le jeu complet. Il s'ouvre aussi dans un navigateur.
- `android/` : l'app Android, qui affiche ce jeu hors-ligne avec la voix française d'Android.
- `.github/workflows/build-apk.yml` : la compilation dans le cloud.

## Obtenir l'APK

À chaque envoi sur la branche `main`, GitHub Actions compile l'app.
Ouvre l'onglet **Actions**, puis la dernière exécution « Construire l'APK ». Télécharge **PatrouilleCourse-apk** (un fichier .zip qui contient `PatrouilleCourse.apk`).
