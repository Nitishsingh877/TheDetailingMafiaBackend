package com.thederailingmafia.carwash.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private  final MailService mailService;


    public NotificationService(MailService mailService) {
        this.mailService = mailService;
    }

    @RabbitListener(queues = "notifications.queue")
    public void receiveMessage(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String eventType = (String) event.get("event");
            String toEmail  = (String) event.getOrDefault("customerEmail", event.get("washerEmail"));
            String subject = "";
            String templateName = "";
            Map<String,Object> variables = new HashMap<>(event);
            switch (eventType) {
                case "order.created":
                    subject="Order Created - CarWash";
                    templateName="order-created-email";
                    System.out.println("Notification: Order " + event.get("orderId") + " created for customer " + event.get("customerEmail"));
                    break;
                case "order.assigned":
                    subject = "Order Assigned to Washer";
                    templateName = "order-assigned-email";
                   // body = "Order " + event.get("orderId") + " has been assigned to washer.";
                    System.out.println("Notification: Order " + event.get("orderId") + " assigned to washer " + event.get("washerEmail"));
                    break;
                case "order.updated":
                    subject = "Order Status Updated";
                    templateName = "order-updated-email";
                  //  body = "Notification: Order " + event.get("orderId") + " status updated to " + event.get("status") + " for customer " + event.get("customerEmail");
                    System.out.println("Notification: Order " + event.get("orderId") + " status updated to " + event.get("status") + " for customer " + event.get("customerEmail"));
                    break;
                case "order.completed":
                    subject = "Order Completed Successfully";
                    templateName = "order-completed-email";
                   // body = "Notification: Order " + event.get("orderId") + " completed for customer " + event.get("customerEmail");
                    System.out.println("Notification: Order " + event.get("orderId") + " completed for customer " + event.get("customerEmail"));
                    break;
                case "order.not_completed":
                    System.out.println("Notification: Order " + event.get("orderId") + " not completed for customer " + event.get("customerEmail"));
                    break;
                case "washer.accepted":
                    subject = "Order Accepted by Washer";
                    templateName = "washer-accepted-email";
                   // body="Notification: Order " + event.get("orderId") + " accepted by washer " + event.get("washerEmail");
                    System.out.println("Notification: Order " + event.get("orderId") + " accepted by washer " + event.get("washerEmail"));
                    break;
                case "washer.rejected":
                    System.out.println("Notification: Order " + event.get("orderId") + " rejected by washer " + event.get("washerEmail"));
                    break;
                case "washer.invoice_generated":
                    subject = "Invoice Generated for Your Order";
                    templateName = "invoice-generated-email";
                   // body="Notification: Invoice " + event.get("invoiceId") + " generated for order " + event.get("orderId") + " by washer " + event.get("washerEmail") + ", payment ID: " + event.get("paymentId");
                    System.out.println("Notification: Invoice " + event.get("invoiceId") + " generated for order " + event.get("orderId") + " by washer " + event.get("washerEmail") + ", payment ID: " + event.get("paymentId"));
                    break;
                case "payment.processed":
                    System.out.println("Notification: Payment " + event.get("paymentId") + " processed for order " + event.get("orderId") + ", status: " + event.get("status"));
                    break;
                case "payment.confirmed":
                    subject = "Payment Confirmed";
                    templateName = "payment-confirmed-email";
                   // body="Notification: Payment " + event.get("paymentId") + " confirmed for order " + event.get("orderId") + ", amount: " + event.get("amount");
                    System.out.println("Notification: Payment " + event.get("paymentId") + " confirmed for order " + event.get("orderId") + ", amount: " + event.get("amount"));
                    break;
                case "payment.failed":
                    System.out.println("Notification: Payment " + event.get("paymentId") + " failed for order " + event.get("orderId") + ", status: " + event.get("status"));
                    break;
                default:
                    System.out.println("Unknown event: " + eventType);
            }

            mailService.sendTemplateEmail(toEmail,subject,templateName,variables);
            System.out.println("Email sent to: " +  toEmail + " for event " + eventType);
        } catch (Exception e) {
            System.err.println("Error processing notification: " + e.getMessage());
        }
    }
}