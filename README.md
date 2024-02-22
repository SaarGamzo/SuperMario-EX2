# Exercise No. 2 - Super Mario 2

## Specifics:
1. Adjustable number of rows (4-8) and columns (3-5) - default is 8 rows and 5 columns.
2. Adjustable game speed (1-5).
3. Obstacles come at a constant speed on the road (obstacles come towards the player).
4. Crash notification - toast message + vibration + sound.
5. 3 Lives.
6. App icon design.
7. 3 Game mods: buttons-fast, buttons-slow, sensors.
8. Sensor mode contains the ability to tilt right/left to move the player.
9. Sensor mode contains the ability to tilt up/down to change game speed.

## New implementations:
* Using ***Fragments*** - one fragment to the players records and one for a map: a click on specific record will show the player's location on the map
* Using ***SharedPreferences*** - the player's records are stored on the device's SharedPreferences
* Using ***Google maps API*** - fetching google maps
* Using ***Device's location services*** - for setting the longitude and altitude of the players current location
* Using ***MediaPlayer*** - Used for crash sound ('Ouch')
* Using ***Adapter & RecyclerView*** - to the Top Ten highscores: designed one object and than by the RecyclerView creates 10 duplications of it

## Game Flow:
- **OnPause:** Freezes when exiting the app.
- **OnResume:** Continues from where it paused when returning to the app.
- **Timer:** Scheduled appearance of obstacles.
- **Vibrator & Toast:** Used for every crash.

## Menus screenshots:

| Main Menu | Settings | Game modes | ScoreBoard |
| --- | --- | --- | --- |
| <img src="media/MainMenu.jpeg" alt="Main Menu" width="200"/> | <img src="media/settings.jpeg" alt="Settings" width="200"/> | <img src="media/GameMenu.jpeg" alt="Game modes" width="200"/> | <img src="media/ScoreBoard.jpeg" alt="ScoreBoard" width="200"/> |

## In-Game screenshots:

| Buttons Mode | Sensor Mode | Pause Dialog | Game Over Dialog |
| --- | --- | --- | --- |
| <img src="media/ButtonsGame.jpeg" alt="Buttons Mode" width="200"/> | <img src="media/SensorGame.jpeg" alt="Sensor Mode" width="200"/> | <img src="media/PauseDialog.jpeg" alt="Pause Dialog" width="200"/> | <img src="media/GameOverDialog.jpeg" alt="Game Over Dialog" width="200"/> |

## Gifs Previews:

| Buttons Game Preview | Sensor Game Preview | ScoreBoard Preview |
| --- | --- | --- |
| <img src="https://github.com/SaarGamzo/SuperMario-EX2/assets/88244818/985ba927-a80d-4d39-9728-c38cf4167a1b" alt="Buttons Game Preview" width="200"/> | <img src="https://github.com/SaarGamzo/SuperMario-EX2/assets/88244818/6c2ae249-6c87-40ba-a849-71507a2b9d53" alt="Sensor Game Preview" width="200"/> | <img src="https://github.com/SaarGamzo/SuperMario-EX2/assets/88244818/99718729-7bf9-4d26-be70-e43772b79628" alt="ScoreBoard Preview" width="200"/> |

## Video Previews:

### Buttons mode game:
https://github.com/SaarGamzo/SuperMario-EX2/assets/88244818/9250696f-d607-4fc3-aab1-223b1163a953
### Sensor mode game:
https://github.com/SaarGamzo/SuperMario-EX2/assets/88244818/3a54488b-f506-4869-8b8a-52ff40763ade
### ScoreBoard:
https://github.com/SaarGamzo/SuperMario-EX2/assets/88244818/64b81335-bc46-4e6b-84e2-1a9fd07be60a
