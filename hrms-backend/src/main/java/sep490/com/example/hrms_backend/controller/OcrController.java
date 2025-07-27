package sep490.com.example.hrms_backend.controller;

import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.com.example.hrms_backend.dto.EmployeeOCRResponseDTO;
import sep490.com.example.hrms_backend.service.OcrService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/scan-cccd")
    public ResponseEntity<EmployeeOCRResponseDTO> scanCCCD(
            @RequestParam(value = "front", required = false) MultipartFile frontImage,
            @RequestParam(value = "back", required = false) MultipartFile backImage) {
        return ResponseEntity.ok(ocrService.scanCCCDWithCloudVision(frontImage, backImage));
    }

    @GetMapping("/face-image")
    public ResponseEntity<byte[]> getFaceImageFromFrontCCCD(@RequestParam("front") MultipartFile frontImage) throws IOException {
        AnnotateImageResponse res = annotateImage(frontImage);

        if (res.getFaceAnnotationsList().isEmpty()) {
            return ResponseEntity.badRequest().body("No face detected".getBytes());
        }

        BoundingPoly box = res.getFaceAnnotations(0).getFdBoundingPoly();

        // Parse bounding box to get min/max coordinates
        int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE;

        for (Vertex v : box.getVerticesList()) {
            if (v.getX() < xMin) xMin = v.getX();
            if (v.getX() > xMax) xMax = v.getX();
            if (v.getY() < yMin) yMin = v.getY();
            if (v.getY() > yMax) yMax = v.getY();
        }

        // Crop face from image
        BufferedImage original = ImageIO.read(frontImage.getInputStream());
        BufferedImage face = original.getSubimage(xMin, yMin, xMax - xMin, yMax - yMin);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(face, "jpg", baos);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=face.jpg")
                .contentType(MediaType.IMAGE_JPEG)
                .body(baos.toByteArray());
    }

    private AnnotateImageResponse annotateImage(MultipartFile file) throws IOException {
        ByteString imgBytes = ByteString.readFrom(file.getInputStream());

        com.google.cloud.vision.v1.Image img = com.google.cloud.vision.v1.Image.newBuilder()
                .setContent(imgBytes)
                .build();

        Feature textFeature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        Feature faceFeature = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();

        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(textFeature)
                .addFeatures(faceFeature)
                .setImage(img)
                .build();

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(Collections.singletonList(request));
            AnnotateImageResponse res = response.getResponses(0);
            if (res.hasError()) {
                throw new RuntimeException("Cloud Vision API error: " + res.getError().getMessage());
            }
            return res;
        }
    }
}
