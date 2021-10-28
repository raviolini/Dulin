package Dulin.engine.exceptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/*
  R class shorten of Resource,
  a helper class for anything corelated with resources
  especially simplifying to get the resources path
  */
public class R {

  /*public static void main(String[] args) {
    System.out.println(getTexturePathByName("grass.png"));
  }*/

  static String basePath = "src/main/resources/images/";
  private static ArrayList<String> texturesPath = new ArrayList<>();


  public static String getImagesPathByName(String filename){

    return basePath + filename;
  }

  public static String getTexturePathByName(String filename){
    loadPath();
    String filePathRes = "";
    for (String textureDir : texturesPath) {
      String filePath = basePath + textureDir + filename;
      if (Files.exists(Path.of(filePath))) {
        filePathRes = filePath;
      }
    }
    return filePathRes;
  }

  private static void loadPath(){
    String[] paths = {"building", "items", "land", "wotah"};
    String adder = "-textures/";
    for (String path : paths){
      texturesPath.add(path + adder);
    }
  }

}
