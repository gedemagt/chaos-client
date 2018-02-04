package com.jhalkjar.caoscomp.gui;

import com.codename1.capture.Capture;
import com.codename1.components.ImageViewer;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.Util;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.ImageIO;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.Validator;
import com.jhalkjar.caoscomp.backend.Grade;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.database.DB;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;


/**
 * Created by jesper on 11/5/17.
 */
public class RuteCreator extends Form {

    ImageViewer iv = new ImageViewer();
    String path = FileSystemStorage.getInstance().getAppHomePath() + "TEMP_IMAGE.jpg";
    boolean imageLoaded = false;

    public RuteCreator() {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        TextComponent name = new TextComponent().label("Name");

        GymPicker gym = new GymPicker(this);
        Button b = new Button(FontImage.createMaterial(FontImage.MATERIAL_PHOTO_CAMERA, s));
        b.addActionListener(evt -> {
            takePicture(path);
            loadPicture(path);
        });
        Button b2 = new Button(FontImage.createMaterial(FontImage.MATERIAL_IMAGE, s));
        b2.addActionListener(evt -> {
            browsePicture(path);
        });

        FontImage.createMaterial(FontImage.MATERIAL_CAMERA, s);

        Validator val = new Validator();
        val.addConstraint(name, new LengthConstraint(1));

        add(BorderLayout.NORTH,
                BoxLayout.encloseY(
                        name,
                        gym,
                        BoxLayout.encloseX(new Label("Image"), b, b2)));
        add(BorderLayout.CENTER, iv);
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {
            if(!imageLoaded) Dialog.show("No image", "Please choose an image!", Dialog.TYPE_ERROR, null, "OK", null);
            else {
                Rute r = DB.getInstance().createRute(name.getField().getText(), path, DB.getInstance().getLoggedInUser(), gym.getGym(), new Date(), null, Grade.green);
                new Editor(r).show();
            }
        });
        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });
    }

    private void loadPicture(String path) {
        try {
            Image image = Image.createImage(FileSystemStorage.getInstance().openInputStream(path));
            iv.setImage(image);
        } catch(Exception ex){
            Log.e(ex);
        }
    }

    private void takePicture(String path) {
        String filePath = Capture.capturePhoto();
        if (filePath != null) {
            try {
                Image img = Image.createImage(filePath);
                OutputStream os = FileSystemStorage.getInstance().openOutputStream(path);
                ImageIO.getImageIO().save(img, os, ImageIO.FORMAT_JPEG, 0.9f);
                imageLoaded = true;
                os.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void browsePicture(String path) {
        Display.getInstance().openGallery(ev-> {

            if (ev != null && ev.getSource() != null) {
                String picturePath = (String) ev.getSource();
                try {
                    Util.copy(FileSystemStorage.getInstance().openInputStream(picturePath), FileSystemStorage.getInstance().openOutputStream(path));
                    imageLoaded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loadPicture(path);
            }

        }, Display.GALLERY_IMAGE);
    }


}
