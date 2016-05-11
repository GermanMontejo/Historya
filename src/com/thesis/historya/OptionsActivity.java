package com.thesis.historya;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.ui.activity.BaseGameActivity;

public class OptionsActivity extends BaseGameActivity implements IOnMenuItemClickListener {

    @Override
    public void onLoadComplete() {
    }

    @Override
    public Engine onLoadEngine() {
        return null;
    }

    @Override
    public void onLoadResources() {
    }

    @Override
    public Scene onLoadScene() {
        return null;
    }

    @Override
    public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1, float arg2, float arg3) {
        return false;
    }

}
