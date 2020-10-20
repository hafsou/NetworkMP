package fr.istic.mob.networkMP;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class PathSerializer implements JsonSerializer<CustomPath> {

    @Override
    public JsonElement serialize(CustomPath src, Type typeOfSrc, JsonSerializationContext context) {
        //operation to do
        JsonObject jsonPath = new JsonObject();
        jsonPath.addProperty("xStart", src.getPathPoints().get(0)[0]);
        jsonPath.addProperty("yStart", src.getPathPoints().get(0)[1]);
        jsonPath.addProperty("xFinal", src.getPathPoints().get(1)[0]);
        jsonPath.addProperty("yFinal", src.getPathPoints().get(1)[1]);
        return jsonPath;
    }


}
