package ChaosMonkey;

import Model.Model;

import java.io.*;
import java.util.Base64;
import java.util.Random;

import static java.lang.Thread.sleep;


public class ChaosMonkey {

    private Model model;
    private final float prob = (float) 0.02087121525;
    private final String base64text = "UGVuZ3VpbnMgYXJIIGdldHRpbmcgaG90";

    public ChaosMonkey (Model model) {
        this.model = model;
    }

    public float Operation (float v1, float v2, char o) {
        Random random = new Random();
        float r = random.nextFloat();
        if (r < prob) {
            return 100;
        } else {
            switch (o){
                case '+':
                    return v1 + v2;
                case '-':
                    return v1 - v2;
                case 'x':
                    return v1 * v2;
                case '/':
                    return v1 / v2;
            }
        }


        return Float.parseFloat(null);
    }


    public void readFile(String file) {
        try {
            InputStream f = new FileInputStream(file);
            System.out.println(readFromInputStream(f));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /**
     *
     * @param path
     * @param actua indica si ha d'actuar o no ChaosMonkey
     * @return
     */
    public String encoderBase64(String path, boolean actua) {
        File file;
        String base64Image = "";
        Random random = new Random();
        float r = random.nextFloat();
        if (r < prob && actua) {
            file = new File(model.getWrongPath());
        } else {
            file = new File(path);
        }
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            base64Image = Base64.getEncoder().encodeToString(imageData);
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
        return base64Image;
    }

    public byte[] decoderBase64(String base64Image, String pathFile) {
        try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
            // Converting a Base64 String into Image byte array
            byte[] imageByteArray = Base64.getDecoder().decode(base64Image);


            Random random = new Random();
            float r = random.nextFloat();
            if (r < prob) {
                imageByteArray[imageByteArray.length - Math.round(imageByteArray.length * random.nextFloat())] = (byte)Math.round(127 * random.nextFloat());
            }

            imageOutFile.write(imageByteArray);
            return imageByteArray;
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        } catch (Exception ex) {
            System.out.println("Problema al decodificar la imatge");
        }
        return null;
    }

    public byte[] getByteArrayFromFile(String path, boolean actua) {
        File file;
        Random random = new Random();
        float r = random.nextFloat();
        if (r < 0.3 && actua) {
            file = new File(model.getWrongPath());
        } else {
            file = new File(path);
        }
        try (FileInputStream imageInFile = new FileInputStream(file)) {
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
            r = random.nextFloat();
            if (r < prob && actua) {
                imageData[imageData.length - Math.round(imageData.length * random.nextFloat())] = (byte)Math.round(127 * random.nextFloat());
            }
            return imageData;
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
        return null;

    }

    public void writeObject(Object o, ObjectOutputStream oos) {

        try {
            Random random = new Random();
            if(o instanceof String) {
                float r = random.nextFloat();
                if (r < prob) {
                    o = o.toString().substring(0,o.toString().length()/2) + "ñ" + o.toString().substring(o.toString().length()/2);

                }
            }

            if (o instanceof byte[]) {
                if (random.nextFloat() < prob) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            oos.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readObject(ObjectInputStream ois) {
        try {
            Object o = ois.readObject();
            Random random = new Random();
            float r = random.nextFloat();
            if (r < prob && o instanceof String) {
                o = o.toString().substring(0,o.toString().length()/2) + "p" + o.toString().substring(o.toString().length()/2);
            }

            if (r < prob && o instanceof byte[]) {
                byte[] object = (byte[]) o;
                object[object.length - Math.round(object.length * random.nextFloat())] = (byte)Math.round(127 * random.nextFloat());
            }
            return o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public float getProb () {
        return (prob*5 - prob*prob*10);
    }
}


