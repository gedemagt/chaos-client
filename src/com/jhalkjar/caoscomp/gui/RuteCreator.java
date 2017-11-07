package com.jhalkjar.caoscomp.gui;

import com.codename1.capture.Capture;
import com.codename1.components.ImageViewer;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.ImageIO;
import com.jhalkjar.caoscomp.backend.DBRute;
import com.jhalkjar.caoscomp.database.RuteDatabase;

import java.io.OutputStream;


/**
 * Created by jesper on 11/5/17.
 */
public class RuteCreator extends Form {

    RuteDatabase db = new RuteDatabase();
    String picturePath;
    ImageViewer iv = new ImageViewer();

    public RuteCreator() {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        TextField name = new TextField("NewGame","Name of the game!", 20, TextArea.ANY);
        //base = new TextField("","Image", 10, TextArea.ANY);
        TextField more = new TextField("2000","Creator", 20, TextArea.ANY);
        Button b = new Button(FontImage.createMaterial(FontImage.MATERIAL_PHOTO_CAMERA, s));
        b.addActionListener(evt -> {
            picturePath = takePicture();
            loadPicture();
        });
        Button b2 = new Button(FontImage.createMaterial(FontImage.MATERIAL_IMAGE, s));
        b2.addActionListener(evt -> {
            browsePicture();
        });

        FontImage.createMaterial(FontImage.MATERIAL_CAMERA, s);

        add(BorderLayout.NORTH,
                BoxLayout.encloseY(
                        new Label("Name"),
                        name,
                        new Label("Creator"),
                        more,
                        BoxLayout.encloseX(new Label("Image"), b, b2)));
        add(BorderLayout.CENTER, iv);
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {

            DBRute r = db.createRute(name.getText(), picturePath, more.getText());
            new Editor(r).show();

        });
        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });
    }

    private void loadPicture() {
        try {
            Image image = Image.createImage(FileSystemStorage.getInstance().openInputStream(picturePath));
            iv.setImage(image);
        } catch(Exception ex){
            Log.e(ex);
        }
    }

    private String takePicture() {
        String filePath = Capture.capturePhoto();
        String pathToBeStored = null;
        if (filePath != null) {
            try {
                pathToBeStored = FileSystemStorage.getInstance().getAppHomePath() + System.currentTimeMillis() +  ".jpg";
                Image img = Image.createImage(filePath);
                OutputStream os = FileSystemStorage.getInstance().openOutputStream(pathToBeStored );
                ImageIO.getImageIO().save(img, os, ImageIO.FORMAT_JPEG, 0.9f);
                os.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pathToBeStored;
    }

    private void browsePicture() {
        Display.getInstance().openGallery(ev-> {

            if (ev != null && ev.getSource() != null) {
                picturePath = (String) ev.getSource();
                loadPicture();
            }

        }, Display.GALLERY_IMAGE);
    }


}
