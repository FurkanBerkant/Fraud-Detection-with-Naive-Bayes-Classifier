package com.example.tez.controllers;
import com.example.tez.model.CardFraudInput;
import com.example.tez.service.concretes.CardTransDataService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;


@RestController
@RequestMapping("/api/v1/card_fraud")
@AllArgsConstructor
public class CardTransDataController {


    private CardTransDataService cardTransDataService;

    @PostMapping("/predict-fraud")
    public ResponseEntity<String> predictFraudPost(@RequestBody CardFraudInput input) {
        try {
            double distanceFromHome = input.getDistanceFromHome();
            double distanceFromLastTransaction = input.getDistanceFromLastTransaction();
            double ratioToMedianPurchasePrice = input.getRatioToMedianPurchasePrice();
            double repeatRetailer = input.getRepeatRetailer();
            double usedChip = input.getUsedChip();
            double usedPinNumber = input.getUsedPinNumber();
            double onlineOrder = input.getOnlineOrder();

            boolean isFraud=cardTransDataService.predictFraud(distanceFromHome, distanceFromLastTransaction, ratioToMedianPurchasePrice,
                    repeatRetailer, usedChip, usedPinNumber, onlineOrder);

            // Sahte veya gerçek işlemi tahmin et
            if (isFraud) {
                return ResponseEntity.ok("Sahte işlem");
            } else {
                return ResponseEntity.ok("Gerçek işlem");
            }
        } catch (Exception e) {
            // Tahmin işleminde bir hata oluştu
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/")
    public ResponseEntity<String> classifyWeather() {
        try {
            String result = cardTransDataService.cardTraining();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
        }
    }



}
