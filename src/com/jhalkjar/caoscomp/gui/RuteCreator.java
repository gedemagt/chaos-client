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
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.util.ImageIO;
import com.jhalkjar.caoscomp.backend.Gym;
import com.jhalkjar.caoscomp.backend.Rute;
import com.jhalkjar.caoscomp.backend.UnknownGym;
import com.jhalkjar.caoscomp.backend.UnknownUser;
import com.jhalkjar.caoscomp.database.DB;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;


/**
 * Created by jesper on 11/5/17.
 */
public class RuteCreator extends Form {

    String picturePath;
    ImageViewer iv = new ImageViewer();
    List<Gym> gyms;

    public RuteCreator() {
        super(new BorderLayout());
        Style s = UIManager.getInstance().getComponentStyle("Title");

        TextField name = new TextField("NewRute","Name of the Rute!", 20, TextArea.ANY);

        gyms = DB.getInstance().getGyms();
        gyms.add(new UnknownGym());
        Picker gym = createGymPicker();
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
                        new Label("Gym"),
                        gym,
                        BoxLayout.encloseX(new Label("Image"), b, b2)));
        add(BorderLayout.CENTER, iv);
        getToolbar().addCommandToRightBar("", FontImage.createMaterial(FontImage.MATERIAL_DONE, s), (e) -> {

            Rute r = DB.getInstance().createRute(name.getText(), picturePath, new UnknownUser(), gyms.get(gym.getSelectedStringIndex()), new Date());
            new Editor(r).show();
        });
        getToolbar().addCommandToLeftBar("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, s), (e) -> {
            new RuteList().showBack();
        });
    }

    String[] gymToString(List<Gym> gyms) {
        String[] r = new String[gyms.size()];
        for(int i=0; i<gyms.size(); i++) {
            r[i] = gyms.get(i).getName();
        }
        return r;
    }

    private Picker createGymPicker() {
        Picker stringPicker = new Picker();
        stringPicker.setType(Display.PICKER_TYPE_STRINGS);

        stringPicker.setStrings(gymToString(gyms));
        stringPicker.setSelectedStringIndex(0);

        return stringPicker;
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
