package Network;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfiguracioServer {

    private int portClient;


    public int getPortClient() {
        return portClient;
    }

    public void setPortClient(int portClient) {
        this.portClient = portClient;
    }


    public ConfiguracioServer llegeixJsonServidor () throws FileNotFoundException {

        ConfiguracioServer config;

        //En primer lloc, s'obtindrà el path actual.

        String f = new File("").getAbsolutePath();
        Gson gson = new Gson();

        //En segon lloc, s'obrirà el JSON corresponent.

        File file = new File(f.concat("/roots/server.json"));

        JsonReader reader = new JsonReader(new FileReader(f.concat("/roots/server.json")));
        config = gson.fromJson(reader, ConfiguracioServer.class);

        return config;

    }

}
