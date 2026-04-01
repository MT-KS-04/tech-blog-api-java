package com.mtks04.tech_blog_api.config;

import com.mtks04.tech_blog_api.entity.Category;
import com.mtks04.tech_blog_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // Nếu chưa có category nào trong database, tạo sẵn một vài cái để tác giả chọn
        if (categoryRepository.count() == 0) {
            Category c1 = Category.builder()
                    .name("Lập trình")
                    .slug("lap-trinh")
                    .description("Nơi chia sẻ kiến thức về lập trình.")
                    .build();
            Category c2 = Category.builder()
                    .name("Trí tuệ nhân tạo")
                    .slug("tri-tue-nhan-tao")
                    .description("Các chuyên mục nâng cao về AI/Machine Learning")
                    .build();
            Category c3 = Category.builder()
                    .name("Công nghệ")
                    .slug("cong-nghe")
                    .description("Tin tức công nghệ nói chung")
                    .build();
            Category c4 = Category.builder()
                    .name("Review")
                    .slug("review")
                    .description("Đánh giá các sản phẩm")
                    .build();
            
            categoryRepository.saveAll(Arrays.asList(c1, c2, c3, c4));
        }
    }
}
