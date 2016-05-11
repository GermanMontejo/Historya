package com.thesis.historya;

import org.anddev.andengine.entity.scene.Scene;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class GameUtils extends Application {
    SharedPreferences positionPref;
    SharedPreferences audioOptions;
    SharedPreferences riddlePref;
    SharedPreferences heroPref;
    Editor editor;
    boolean hintOn = false;
    private int healthRate;
    public static enum Riddles {
        RIDDLE1, RIDDLE2, RIDDLE3, RIDDLE4, SECRET_CODE, RIDDLE5, RIDDLE6, RIDDLE7, RIDDLE8, RIDDLE9
    }

    public final static String riddle1Answer = "clothesline";

    public GameUtils(Context context) {
        positionPref = context.getSharedPreferences("positionPref", Context.MODE_PRIVATE);
        audioOptions = PreferenceManager.getDefaultSharedPreferences(context);
        riddlePref = PreferenceManager.getDefaultSharedPreferences(context);
        heroPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = positionPref.edit();
    }

    public String getLastPlace() {
        return positionPref.getString("lastPlace", "com.thesis.historya.Barrio1Terrain1Activity");
    }

    public void setLastPlace(String lastPlace) {
        editor.putString("lastPlace", lastPlace).commit();
    }

    public void setX(float heroX) {
        editor.putFloat("heroX", heroX).commit();
    }

    public void setY(float heroY) {
        editor.putFloat("heroY", heroY).commit();
    }

    public float getX() {
        return positionPref.getFloat("heroX", 10);
    }

    public float getY() {
        return positionPref.getFloat("heroY", 60);
    }

    public void setMusicOn() {
        audioOptions.edit().putBoolean("musicOn", true).commit();
    }

    public void setMusicOff() {
        audioOptions.edit().putBoolean("musicOn", false).commit();
    }

    public void setHintOn() {
        audioOptions.edit().putBoolean("hintOn", true).commit();
    }

    public void setHintOff() {
        audioOptions.edit().putBoolean("hintOn", false).commit();
    }

    public boolean isHintOn() {
        return audioOptions.getBoolean("hintOn", false);
    }

    public void setSoundEffectOn() {
        audioOptions.edit().putBoolean("soundOn", true).commit();
    }

    public void setSoundEffectOff() {
        audioOptions.edit().putBoolean("soundOn", false).commit();
    }

    public boolean isMusicOn() {
        return audioOptions.getBoolean("musicOn", false);
    }

    public boolean isSoundOn() {
        return audioOptions.getBoolean("soundOn", false);
    }

    public String getAnswer(String riddle) {
        return riddlePref.getString(riddle, "");
    }

    public void setAnswer(String riddle, String answer) {
        riddlePref.edit().putString(riddle, answer).commit();
    }

    public boolean hasQuestionBeenAnswered(String riddle) {
        if (riddlePref.getBoolean(riddle + "Answer", false)) {
            return true;
        }
        return false;
    }

    public void setQuestionAnswered(String riddle, boolean value) {
        riddlePref.edit().putBoolean(riddle + "Answer", value).commit();
    }

    public boolean compareAnswer(Riddles riddle, String answer) {
        String answerForRiddle = getAnswer(riddle.toString());
        switch (riddle) {
        case RIDDLE1:

            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase()) || answerForRiddle.toLowerCase().contains(answer.toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE2:

            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE3:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase()) || answer.toLowerCase().equals("Masonry".toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE4:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase()) || answer.toLowerCase().equals("Mountain Buntis".toLowerCase())) {
                return true;
            }
            break;
        case SECRET_CODE:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE5:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase()) || answer.toLowerCase().equals("Negritos".toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE6:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase()) || answer.toLowerCase().equals("Philip II of Spain".toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE7:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase()) || answer.toLowerCase().equals("classic".toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE8:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase())) {
                return true;
            }
            break;
        case RIDDLE9:
            if (answerForRiddle.toLowerCase().equals(answer.toLowerCase())) {
                return true;
            }
            break;
        }
        return false;
    }
    public void setHealthRate(int healthRate) {
        this.healthRate = healthRate;
        heroPref.edit().putInt("healthRate", healthRate).commit();
    }

    public int getHealthRate() {
        return heroPref.getInt("healthRate", 0);
    }

    public void setEnemyKilled(String level) {
        String key = level + "EnemyKilled";
        heroPref.edit().putBoolean(key, true).commit();
    }

    public boolean isEnemyDead(String level) {
        String key = level + "EnemyKilled";
        if (heroPref.getBoolean(key, false)) {
            // if levelEnemyKilled doesn't return true, that means the enemy
            // hasn't been killed yet
            return true;
        }
        return false;
    }

    public void setLevelPassed(String level) {
        heroPref.edit().putBoolean(level + "Passed", true).commit();
    }

    public boolean getLevelPassed(String level) {
        return heroPref.getBoolean(level + "Passed", false);
    }

    public int getEnemyLife(String level) {
        return heroPref.getInt(level + "Enemy", -5);
    }

    public void setEnemyLife(String level, int enemyHealthRate) {
        heroPref.edit().putInt(level + "Enemy", enemyHealthRate).commit();
    }

    public boolean isRockGolemDead(String level){
        String key = level + "RockGolemKilled";
        if (heroPref.getBoolean(key, false)) {
            // if levelEnemyKilled doesn't return true, that means the enemy
            // hasn't been killed yet
            return true;
        }
        return false;
    }

    public void setRockGolemKilled(String level) {
        String key = level + "RockGolemKilled";
        heroPref.edit().putBoolean(key, true).commit();
    }

    public int getRockGolemLife(String level) {
        return heroPref.getInt(level + "RockGolem", -5);
    }

    public void setRockGolemLife(String level, int enemyHealthRate) {
        heroPref.edit().putInt(level + "RockGolem", enemyHealthRate).commit();
    }

    public void setPickedApple(String apple, boolean pickedApple) {
        heroPref.edit().putBoolean(apple, pickedApple).commit();
    }

    public boolean pickedApple() {
        if (heroPref.getBoolean("apple", false)) {
            return true;
        }
        return false;
    }

    public void setLearnSpell(String spell) {
        heroPref.edit().putBoolean(spell, true).commit();
    }
    public boolean doesHeroKnowSpell(String spell) {
        if (heroPref.getBoolean(spell, false)) {
            return true;
        }
        return false;
    }
    public boolean doesSoilHaveWater() {
        if (heroPref.getBoolean("waterRestored", false)) {
            return true;
        }
        return false;
    }
    public void setWaterOnSoil() {
        heroPref.edit().putBoolean("waterRestored", true).commit();
    }
    public void teachKidWaterMagic() {
        heroPref.edit().putBoolean("teachWaterMagic", true).commit();
    }
    public boolean doesKidKnowWaterMagic() {
        if (heroPref.getBoolean("teachWaterMagic", false)) {
            return true;
        }
        return false;
    }
    public boolean doesTheHeroHaveTheKey() {
        if (heroPref.getBoolean("heroHasTheKey", false)) {
            return true;
        }
        return false;
    }
    public void setHeroHasTheKey() {
        heroPref.edit().putBoolean("heroHasTheKey", true).commit();
    }

    public void storeUsername(String username) {
        heroPref.edit().putString("username", username).commit();
    }

    public String retrieveUsername() {
        return heroPref.getString("username", "");
    }

    public void clearUsername() {
        heroPref.edit().remove("username").commit();
    }

    public void gameDone(Scene scene) {
        scene.setIgnoreUpdate(true);
        if (!hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE9.toString())) {
            // todo add something useful here...
        } else {
            heroPref.edit().clear().commit();
        }
    }

    public void gameOver(Scene scene){
        positionPref.edit().clear().commit();
        riddlePref.edit().clear().commit();
        scene.setIgnoreUpdate(true);

    }
}
