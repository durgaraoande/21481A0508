package com.__508.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.__508.demo.model.Product;

import reactor.core.publisher.Flux;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Value("${ecommerce.api.base.url}")
    private String baseUrl;

    @Value("${ecommerce.api.token}")
    private String apiToken;

    private final WebClient webClient;
    
    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Product> getProducts() {
        return this.webClient.get()
                .uri("/companies/AMZ/categories/Laptop/products")
                .header("Authorization", "Bearer " + apiToken)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    public List<Product> getTopProducts(String categoryName, int n, int page, String sortBy, String sortDirection, Double minPrice, Double maxPrice) {
        List<Product> topProducts = new ArrayList<>();

        // List of e-commerce companies
        String[] companies = {"AMZ", "FLP", "SNP", "MYN", "AZO"};

        for (String company : companies) {
            String url = String.format("%s/companies/%s/categories/%s/products?top=%d&minPrice=%f&maxPrice=%f",
                    baseUrl, company, categoryName, n, minPrice != null ? minPrice : 0, maxPrice != null ? maxPrice : Double.MAX_VALUE);

            List<Product> products = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Product.class)
                    .collectList()
                    .block();

            // Process and add to top products list
            if (products != null) {
                for (Product product : products) {
                    product.setId(UUID.randomUUID().toString());
                }
                topProducts.addAll(products);
            }
        }

        // Sorting
        if (sortBy != null) {
            Comparator<Product> comparator = getComparator(sortBy, sortDirection);
            if (comparator != null) {
                topProducts.sort(comparator);
            }
        }

        // Pagination
        int start = (page - 1) * n;
        int end = Math.min(start + n, topProducts.size());
        if (start > end) {
            return new ArrayList<>();
        }

        return topProducts.subList(start, end);
    }

    private Comparator<Product> getComparator(String sortBy, String sortDirection) {
        Comparator<Product> comparator = null;

        switch (sortBy) {
            case "price":
                comparator = Comparator.comparing(Product::getPrice);
                break;
            case "rating":
                comparator = Comparator.comparing(Product::getRating);
                break;
            case "discount":
                comparator = Comparator.comparing(Product::getDiscount);
                break;
            case "company":
                comparator = Comparator.comparing(Product::getCompany);
                break;
        }

        if (comparator != null && "desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    public Product getProductById(String categoryName, String productId) {
        // Assuming products are cached or stored in a map after fetching
        // This is a simplistic approach and can be enhanced to fetch the specific product if needed
        for (String company : Arrays.asList("AMZ", "FLP", "SNP", "MYN", "AZO")) {
            String url = String.format("%s/companies/%s/categories/%s/products?top=1000",
                    baseUrl, company, categoryName);

            List<Product> products = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Product.class)
                    .collectList()
                    .block();

            if (products != null) {
                for (Product product : products) {
                    if (product.getId().equals(productId)) {
                        return product;
                    }
                }
            }
        }

        return null;
    }
}
