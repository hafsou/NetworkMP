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
        jsonPath.addProperty("xStart", src.getxStart());
        jsonPath.addProperty("yStart", src.getyStart());
        jsonPath.addProperty("xFinal", src.getxFinal());
        jsonPath.addProperty("yFinal", src.getyFinal());
        return jsonPath;
    }


}
