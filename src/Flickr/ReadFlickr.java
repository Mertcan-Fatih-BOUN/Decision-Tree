package Flickr;

import misc.Instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by mertcan on 3.3.2016.
 */
public class ReadFlickr {
    public String path_features = "C:\\Users\\mertcan\\Desktop\\flickr\\features";
    public String path_features_edge = path_features + "\\features edgehistogram30k";
    public String path_features_homo =  path_features + "\\features homogeneoustexture30k";
    public String path_tags = path_features + "\\tags";
    public HashMap<String, Integer> tags_index = new HashMap<>();
    public HashMap<String, Integer> tags_occurence = new HashMap<>();
//    public String[] labels = new String[]{"bird_r1.txt", "baby_r1.txt", "car_r1.txt", "clouds_r1.txt", "dog_r1.txt", "female_r1.txt",
//            "flower_r1.txt", "male_r1.txt", "night_r1.txt", "people_r1.txt", "portrait_r1.txt", "river_r1.txt", "sea_r1.txt",
//            "tree_r1.txt"};
    public String[] labels = new String[]{"bird.txt", "baby.txt", "animals.txt", "car.txt", "clouds.txt", "dog.txt", "female.txt", "flower.txt",
            "food.txt", "indoor.txt", "lake.txt", "male.txt", "night.txt", "people.txt", "plant_life.txt", "portrait.txt","river.txt", "sea.txt",
            "sky.txt", "structures.txt", "sunset.txt", "transport.txt", "tree.txt", "water.txt"};
//    public String[] labels = new String[]{"bird.txt", "baby.txt", "animals.txt", "car.txt", "clouds.txt", "dog.txt", "female.txt", "flower.txt",
//            "food.txt", "indoor.txt", "lake.txt", "male.txt", "night.txt", "people.txt", "plant_life.txt", "portrait.txt","river.txt", "sea.txt",
//            "sky.txt", "structures.txt", "sunset.txt", "transport.txt", "tree.txt", "water.txt", "bird_r1.txt", "baby_r1.txt", "car_r1.txt", "clouds_r1.txt", "dog_r1.txt", "female_r1.txt",
//            "flower_r1.txt", "male_r1.txt", "night_r1.txt", "people_r1.txt", "portrait_r1.txt", "river_r1.txt", "sea_r1.txt",
//            "tree_r1.txt"};

    public ArrayList<String> get_class_names_flickr(){
        ArrayList<String> class_names = new ArrayList<>();
        for(int i = 0; i < labels.length; i++){
            class_names.add(labels[i].substring(0, labels[i].length() - 4));
        }
        class_names.add("not_given");
        return class_names;
    }

    public ArrayList<Utils.Instance> get_flickr_instances_util(){
        ArrayList<Utils.Instance> instances = new ArrayList<>();
        ArrayList<ArrayList<String>> all_labels = read_labels(labels);
        ArrayList<String> class_names = get_class_names_flickr();
        ArrayList<ArrayList<String>> edge = read_all_attributes(path_features_edge);
        ArrayList<ArrayList<String>> homo = read_all_attributes(path_features_homo);
        ArrayList<ArrayList<String>> tags = read_tags(path_tags);
        boolean read_tags = true;
        /////////////////////////
//        read_tags = false;

        if(edge.size() != 25000 || homo.size() != 25000 || tags.size() != 25000 || all_labels.size() != 25000){
            System.out.println("error in sizes");
            return null;
        }else{
            System.out.println("reading is done");
        }
        for(int i = 0; i < 25000; i++){
            ArrayList<Double> attributes = new ArrayList<>();
            ArrayList<String> edge_ = edge.get(i);
            ArrayList<String> homo_ = homo.get(i);
            ArrayList<String> tags_ = tags.get(i);
            for(int j = 0; j < edge_.size(); j++){
                attributes.add(Double.parseDouble(edge_.get(j)));
            }
            for(int j = 0; j < homo_.size(); j++){
                attributes.add(Double.parseDouble(homo_.get(j)));
            }

            if(read_tags) {
                int[] binary_tags = new int[tags_index.size()];
                Arrays.fill(binary_tags, 0);
//                System.out.println(tags_index.size() + "  " + i);
                for (int j = 0; j < tags_.size(); j++) {
                    if (tags_index.containsKey(tags_.get(j)))
                        binary_tags[tags_index.get(tags_.get(j))] = 1;
                }
                for (int j = 0; j < binary_tags.length; j++) {
                    attributes.add(binary_tags[j] * 1.0);
                }
            }
            double[] atts = new double[attributes.size()];
            for(int j = 0; j < atts.length; j++){
                atts[j] = attributes.get(j);
            }
            ArrayList<Integer> label_numbers = new ArrayList<>();
            for(int j = 0; j < all_labels.get(i).size(); j++)
                label_numbers.add(class_names.indexOf(all_labels.get(i).get(j)));
            instances.add(new Utils.Instance(class_names.indexOf(all_labels.get(i).get(0)), label_numbers, atts));
        }

        return instances;

    }

    public ArrayList<Instance> get_flickr_instances(){
        ArrayList<Instance> instances = new ArrayList<>();
        ArrayList<ArrayList<String>> all_labels = read_labels(labels);
        ArrayList<String> class_names = get_class_names_flickr();
        ArrayList<ArrayList<String>> edge = read_all_attributes(path_features_edge);
        ArrayList<ArrayList<String>> homo = read_all_attributes(path_features_homo);
        ArrayList<ArrayList<String>> tags = read_tags(path_tags);
        if(edge.size() != 25000 || homo.size() != 25000 || tags.size() != 25000 || all_labels.size() != 25000){
            System.out.println("error in sizes");
            return null;
        }else{
            System.out.println("reading is done");
        }
        for(int i = 0; i < 25000; i++){
            ArrayList<Double> attributes = new ArrayList<>();
            ArrayList<String> edge_ = edge.get(i);
            ArrayList<String> homo_ = homo.get(i);
            ArrayList<String> tags_ = tags.get(i);
            for(int j = 0; j < edge_.size(); j++){
                attributes.add(Double.parseDouble(edge_.get(j)));
            }
            for(int j = 0; j < homo_.size(); j++){
                attributes.add(Double.parseDouble(homo_.get(j)));
            }

            int[] binary_tags = new int[tags_index.size()];
            Arrays.fill(binary_tags, 0);
            for(int j = 0; j < tags_.size(); j++){
                if(tags_index.containsKey(tags_.get(j)))
                     binary_tags[tags_index.get(tags_.get(j))] = 1;
            }
            for(int j = 0; j < binary_tags.length; j++){
                attributes.add(binary_tags[j] * 1.0);
            }
            double[] atts = new double[attributes.size()];
            for(int j = 0; j < atts.length; j++){
                atts[j] = attributes.get(j);
            }
            ArrayList<Integer> label_numbers = new ArrayList<>();
            for(int j = 0; j < all_labels.get(i).size(); j++)
                label_numbers.add(class_names.indexOf(all_labels.get(i).get(j)));
            instances.add(new Instance(class_names.indexOf(all_labels.get(i).get(0)), label_numbers, atts));
        }

        return instances;

    }

    public ArrayList<ArrayList<String>> read_labels(String[] labels_list){
        ArrayList<ArrayList<String>> all_labels = new ArrayList<>();
        for(int i = 0; i < 25000; i++){
            all_labels.add(new ArrayList<String>());
        }
        for(String s: labels_list) {
            String ss = "C:\\Users\\mertcan\\Desktop\\flickr\\features\\mirflickr25k_annotations_v080\\" + s;
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File(ss));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int image_number = Integer.parseInt(line);
                all_labels.get(image_number - 1).add(s.substring(0, s.length() - 4));
            }
        }
        for(int i = 0; i < 25000; i++){
            if(all_labels.get(i).size() == 0) {
                all_labels.get(i).add("not_given");
            }
        }
        return all_labels;
    }

    public ArrayList<ArrayList<String>> read_all_attributes(String directory_name){
        ArrayList<ArrayList<String>> features = new ArrayList<>();
        int bound = 10000;
        for(int i = 0; i < 3; i++){
            int bound2 = bound;
            if(i == 2)
                bound2 = 5000;
            for(int j = 0; j < bound2; j++){
                features.add(read_attribute_file(directory_name + "\\" + i + "\\" + (i * bound + j) + ".txt"));
            }
        }
        return features;
    }

    public ArrayList<ArrayList<String>> read_tags(String directory_name){
        ArrayList<ArrayList<String>> tags = new ArrayList<>();
        int bound = 25000;
        for(int i = 0; i < bound; i++){
            ArrayList<String> tag = read_attribute_file(directory_name + "\\tags" + (i + 1) + ".txt");
            for(int j = 0; j < tag.size(); j++){
                if(!tags_occurence.containsKey(tag.get(j))){
                    tags_occurence.put(tag.get(j), 1);
                }else{
                    tags_occurence.put(tag.get(j), tags_occurence.get(tag.get(j)) + 1);
                    if(tags_occurence.get(tag.get(j)) == 50){
                        tags_index.put(tag.get(j), tags_index.size());
                    }
                }
            }
            tags.add(tag);
        }
        return tags;
    }

    public ArrayList<String> read_attribute_file(String file_name){
        Scanner scanner = null;
        ArrayList<String> atts = new ArrayList<>();
        try {
            scanner = new Scanner(new File(file_name));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            atts.add(scanner.nextLine());
        }
        return atts;
    }
}
