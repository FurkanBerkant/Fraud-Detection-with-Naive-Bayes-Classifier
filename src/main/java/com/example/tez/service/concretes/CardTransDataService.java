package com.example.tez.service.concretes;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;

@Service
public class CardTransDataService {
    public boolean predictFraud(double distanceFromHome, double distanceFromLastTransaction,
                                 double ratioToMedianPurchasePrice, double repeatRetailer, double usedChip,
                                 double usedPinNumber, double onlineOrder) throws Exception {
        // Eğitilmiş modeli yükle
        NaiveBayes naiveBayes = (NaiveBayes) SerializationHelper.read("C:/cardFraud/card_fraud.model");


        System.out.println(naiveBayes);
        // Yeni örneği oluştur
        Instances dataset = createInstances();
        Instance newInstance = new DenseInstance(7);
        newInstance.setValue(0, distanceFromHome);
        newInstance.setValue(1, distanceFromLastTransaction);
        newInstance.setValue(2, ratioToMedianPurchasePrice);
        newInstance.setValue(3, repeatRetailer);
        newInstance.setValue(4, usedChip);
        newInstance.setValue(5, usedPinNumber);
        newInstance.setValue(6, onlineOrder);
        newInstance.setDataset(dataset);

        double predictedClass = naiveBayes.classifyInstance(newInstance);

        System.out.println("Tahmin edilen sınıf: " + predictedClass);

        // Tahmin edilen sınıfı kontrol et
        if (predictedClass < 0.5) {
            // Sahte işlem
            return true;
        } else {
            // Gerçek işlem
            return false;
        }
    }

    private Instances createInstances() {
        // Attributeleri oluştur
        Attribute distanceFromHome = new Attribute("distance_from_home");
        Attribute distanceFromLastTransaction = new Attribute("distance_from_last_transaction");
        Attribute ratioToMedianPurchasePrice = new Attribute("ratio_to_median_purchase_price");
        Attribute repeatRetailer = new Attribute("repeat_retailer");
        Attribute usedChip = new Attribute("used_chip");
        Attribute usedPinNumber = new Attribute("used_pin_number");
        Attribute onlineOrder = new Attribute("online_order");
        Attribute fraud = new Attribute("fraud");

        // Veri kümesini oluştur
        FastVector<Attribute> attributes = new FastVector<>();
        attributes.addElement(distanceFromHome);
        attributes.addElement(distanceFromLastTransaction);
        attributes.addElement(ratioToMedianPurchasePrice);
        attributes.addElement(repeatRetailer);
        attributes.addElement(usedChip);
        attributes.addElement(usedPinNumber);
        attributes.addElement(onlineOrder);
        attributes.addElement(fraud);

        Instances dataset = new Instances("CardFraud", attributes, 1);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        return dataset;
    }

    public String cardTraining() throws Exception {
        // NaiveBayes sınıflandırıcısını oluştur
        NaiveBayes naiveBayes = new NaiveBayes();

        // Veri kümesinin yolu
        String weatherNominalDataset = "C:/Program Files/Weka-3-8-6/data/card_fraud.arff";

        // Veri kümesini okumak için BufferedReader oluştur
        BufferedReader bufferedReader = new BufferedReader(new FileReader(weatherNominalDataset));

        // Veri kümesi örneklerini oluştur
        Instances datasetInstances = new Instances(bufferedReader);

        // Veri kümesini rastgele karıştır
        datasetInstances.randomize(new java.util.Random(0));

        // Veri kümesini eğitim ve test verilerine ayır
        int trainingDataSize = (int) Math.round(datasetInstances.numInstances() * 0.70);
        int testDataSize = (int) datasetInstances.numInstances() - trainingDataSize;

        // Eğitim verisini oluştur
        Instances trainingInstances = new Instances(datasetInstances, 0, trainingDataSize);

        // Test verisini oluştur
        Instances testInstances = new Instances(datasetInstances, trainingDataSize, testDataSize);

        // Hedef sınıfı belirle
        trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
        testInstances.setClassIndex(testInstances.numAttributes() - 1);

        // BufferedReader'ı kapat
        bufferedReader.close();

        // Sınıflandırıcıyı oluştur
        naiveBayes.buildClassifier(trainingInstances);
        System.out.println(trainingInstances);

        SerializationHelper.write("C:/cardFraud/card_fraud.model", naiveBayes);

        SerializationHelper.read("C:/cardFraud/card_fraud.model");
        // Değerlendirme
        Evaluation evaluation = new Evaluation(trainingInstances);
        evaluation.evaluateModel(naiveBayes, testInstances);

        // Değerlendirme sonuçlarını döndür
        return evaluation.toSummaryString("\nResults", false);
    }

}
