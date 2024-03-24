package com.qrgenerator.campusflora.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@RestController()
@RequestMapping("/v1/api")
public class UploadController {


    @GetMapping("/uploadCheck")
    public ResponseEntity<String> uploadImageAndGenerateQR() throws Exception {

        // Set your Cloudinary credentials

        Dotenv dotenv = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        cloudinary.config.secure = true;
        System.out.println(cloudinary.config.cloudName);

// Upload the image
        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
        Map<String, String> responseData = cloudinary.uploader().upload("D:\\Vibu\\campusflora\\campusflora\\check.png", params1);
        String url = responseData.get("url");
        System.out.println(
                cloudinary.uploader().upload("D:\\Vibu\\campusflora\\campusflora\\check.png", params1));
//        System.out.println(
//                cloudinary.uploader().upload("https://cloudinary-devs.github.io/cld-docs-assets/assets/images/coffee_cup.jpg", params1));
// Get the asset details
//        Map params2 = ObjectUtils.asMap(
//                "quality_analysis", true
//        );
//
//        System.out.println(
//                cloudinary.api().resource("coffee_cup", params2));
        generateQRCode(url);
        return ResponseEntity.status(HttpStatus.OK).body("success");
    }


    public void generateQRCode(String QrCodeTextSample) throws WriterException, IOException {
//        String qrCodeText = "https://drive.google.com/file/d/17i4X8fOmY_ouWXwqfZPUK8gSi0byf94D/view?usp=drive_link";
        String qrCodeText = QrCodeTextSample;
        String filePath = "NeemCheck.png";
        int size = 125;
        String fileType = "png";
        File qrFile = new File(filePath);
        createQRImage(qrFile, qrCodeText, size, fileType);
        System.out.println("DONE");
    }

    private static void createQRImage(File qrFile, String qrCodeText, int size, String fileType)
            throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("image") MultipartFile file) throws IOException, WriterException {
        StringBuilder fileNames = new StringBuilder();
//        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
//        fileNames.append(file.getOriginalFilename());
//        Files.write(fileNameAndPath, file.getBytes());
        file.getBytes();
//        model.addAttribute("msg", "Uploaded images: " + fileNames.toString());


        Dotenv dotenv = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        cloudinary.config.secure = true;
        System.out.println(cloudinary.config.cloudName);

// Upload the image
        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
        Map<String, String> responseData = cloudinary.uploader().upload(file.getBytes(), params1);
        String url = responseData.get("url");
        System.out.println(url);
//        System.out.println(
//                cloudinary.uploader().upload("D:\\Vibu\\campusflora\\campusflora\\check.png", params1));
//        System.out.println(
//                cloudinary.uploader().upload("https://cloudinary-devs.github.io/cld-docs-assets/assets/images/coffee_cup.jpg", params1));
// Get the asset details
//        Map params2 = ObjectUtils.asMap(
//                "quality_analysis", true
//        );
//
//        System.out.println(
//                cloudinary.api().resource("coffee_cup", params2));
        generateQRCode(url);
        return "imageupload/index";
    }
}
