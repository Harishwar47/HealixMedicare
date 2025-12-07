package com.example.clinic.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clinic.model.Appointment;
import com.example.clinic.service.AppointmentService;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    private final AppointmentService appointmentService;

    public PrescriptionController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/download/{appointmentId}")
    public ResponseEntity<byte[]> downloadPrescription(@PathVariable Long appointmentId) {
        try {
            Optional<Appointment> apptOpt = appointmentService.findById(appointmentId);
            
            if (apptOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Appointment appointment = apptOpt.get();
            
            // Generate prescription image
            BufferedImage image = generatePrescriptionImage(appointment);
            
            // Convert to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            
            // Set headers for download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "prescription_" + appointmentId + ".png");
            headers.setContentLength(imageBytes.length);
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private BufferedImage generatePrescriptionImage(Appointment appointment) throws IOException {
        int width = 800;
        int height = 1000;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        
        // Header background
        g2d.setColor(new Color(47, 128, 237));
        g2d.fillRect(0, 0, width, 100);
        
        // Header text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        drawCenteredString(g2d, "Medical Prescription", width, 60);
        
        // Subheader
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCenteredString(g2d, "Healix Medicare Clinic", width, 85);
        
        // Reset to black for content
        g2d.setColor(Color.BLACK);
        int y = 140;
        int leftMargin = 50;
        int lineHeight = 30;
        
        // Doctor Information
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Doctor Information", leftMargin, y);
        y += lineHeight;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String doctorName = appointment.getDoctor() != null ? "Dr. " + appointment.getDoctor().getName() : "Dr. Unknown";
        g2d.drawString("Name: " + doctorName, leftMargin + 20, y);
        y += lineHeight;
        
        String specialty = appointment.getDoctor() != null ? appointment.getDoctor().getSpecialty() : "General Medicine";
        g2d.drawString("Specialty: " + specialty, leftMargin + 20, y);
        y += lineHeight + 10;
        
        // Patient Information
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Patient Information", leftMargin, y);
        y += lineHeight;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String patientName = appointment.getPatientName() != null ? appointment.getPatientName() : "Patient";
        g2d.drawString("Name: " + patientName, leftMargin + 20, y);
        y += lineHeight;
        
        String patientId = appointment.getPatient() != null ? appointment.getPatient().getUsername() : "N/A";
        g2d.drawString("Patient ID: " + patientId, leftMargin + 20, y);
        y += lineHeight;
        
        String appointmentDate = appointment.getDate() != null ? appointment.getDate().toString() : "N/A";
        g2d.drawString("Date: " + appointmentDate, leftMargin + 20, y);
        y += lineHeight + 10;
        
        // Diagnosis
        if (appointment.getDiagnosis() != null && !appointment.getDiagnosis().isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Diagnosis", leftMargin, y);
            y += lineHeight;
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            y = drawWrappedText(g2d, appointment.getDiagnosis(), leftMargin + 20, y, width - 100, lineHeight);
            y += 10;
        }
        
        // Prescription
        if (appointment.getPrescription() != null && !appointment.getPrescription().isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Prescription", leftMargin, y);
            y += lineHeight;
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            y = drawWrappedText(g2d, appointment.getPrescription(), leftMargin + 20, y, width - 100, lineHeight);
            y += 10;
        }
        
        // Doctor Notes
        if (appointment.getDoctorNotes() != null && !appointment.getDoctorNotes().isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("Additional Notes", leftMargin, y);
            y += lineHeight;
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 16));
            y = drawWrappedText(g2d, appointment.getDoctorNotes(), leftMargin + 20, y, width - 100, lineHeight);
        }
        
        // Footer
        y = height - 80;
        g2d.setColor(new Color(100, 100, 100));
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        String generatedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a"));
        drawCenteredString(g2d, "Generated on: " + generatedDate, width, y);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        drawCenteredString(g2d, "This is a computer-generated prescription. No signature required.", width, y + 25);
        
        // Border
        g2d.setColor(new Color(47, 128, 237));
        g2d.drawRect(10, 10, width - 20, height - 20);
        g2d.drawRect(11, 11, width - 22, height - 22);
        
        g2d.dispose();
        
        return image;
    }
    
    private void drawCenteredString(Graphics2D g2d, String text, int width, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        g2d.drawString(text, x, y);
    }
    
    private int drawWrappedText(Graphics2D g2d, String text, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g2d.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            int testWidth = fm.stringWidth(testLine);
            
            if (testWidth > maxWidth && line.length() > 0) {
                g2d.drawString(line.toString(), x, y);
                y += lineHeight;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        if (line.length() > 0) {
            g2d.drawString(line.toString(), x, y);
            y += lineHeight;
        }
        
        return y;
    }
}
