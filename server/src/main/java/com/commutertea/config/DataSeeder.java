package com.commutertea.config;

import com.commutertea.common.GuestNames;
import com.commutertea.common.TransportType;
import com.commutertea.post.Post;
import com.commutertea.post.PostCategory;
import com.commutertea.post.PostRepository;
import com.commutertea.user.User;
import com.commutertea.user.UserRepository;
import com.commutertea.user.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataSeeder(PostRepository postRepository,
                      UserRepository userRepository,
                      BCryptPasswordEncoder passwordEncoder) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 兼容旧库：新增 role 列后已有记录可能为 null，统一补成普通用户。
        userRepository.findAll().forEach(user -> {
            if (user.getRole() == null) {
                user.setRole(UserRole.USER);
                userRepository.save(user);
            }
        });
        seedDefaultUser();
        seedAdminUser();

        if (postRepository.count() > 0) {
            return;
        }

        seed("早上 7:40 的 10 号线，一位阿姨抱着睡着的孩子上车，前排的人默默挪出了一整条通道。",
                PostCategory.FRESH, TransportType.RAIL, true, null, 328, 1);
        seed("每天早高峰都有人把双肩包背在身后，一转身就像一把旋转的锤子。",
                PostCategory.RANT, TransportType.RAIL, true, null, 512, 2);
        seed("今天的日出把整条高架染成橘色，堵车突然也没那么烦了。",
                PostCategory.FRESH, TransportType.BUS, false, "guest_a7k2", 96, 5);
        seed("地铁广播一直说“请往里走”，但门口的人像长在地上一样。",
                PostCategory.RANT, TransportType.RAIL, true, null, 764, 8);
        seed("在 2 号线站务室捡到一张工牌，已经交过去了，失主可以去问问。",
                PostCategory.FRESH, TransportType.RAIL, false, "guest_m3p9", 210, 12);
        seed("公司班车永远比通知时间早三分钟到，晚一分钟就只能目送尾灯。",
                PostCategory.RANT, TransportType.BUS, true, null, 143, 20);
        seed("公交上听到两个陌生人聊《平凡的世界》，突然觉得通勤也有文学时刻。",
                PostCategory.FRESH, TransportType.BUS, false, "guest_k5r1", 88, 26);
        seed("下雨天的共享单车座椅，永远有一滩没人擦的水。",
                PostCategory.RANT, TransportType.BUS, true, null, 402, 30);
    }

    /** 默认体验账号，已存在时不重复创建。 */
    private void seedDefaultUser() {
        if (userRepository.existsByUsername("demo")) {
            return;
        }
        User user = new User();
        user.setUsername("demo");
        user.setNickname("茶客小明");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setTransportType(TransportType.RAIL);
        user.setGender("SECRET");
        user.setBio("上班路上，坐下来聊两句");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /** 管理后台账号：admin / admin123456，已存在时确保其为管理员。 */
    private void seedAdminUser() {
        User user = userRepository.findByUsername("admin").orElseGet(() -> {
            User created = new User();
            created.setUsername("admin");
            created.setNickname("管理员");
            created.setPassword(passwordEncoder.encode("admin123456"));
            created.setTransportType(TransportType.RAIL);
            created.setCreatedAt(LocalDateTime.now());
            return created;
        });
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);
    }

    private void seed(String content,
                      PostCategory category,
                      TransportType transportType,
                      boolean anonymous,
                      String guestId,
                      long viewCount,
                      int hoursAgo) {
        Post post = new Post();
        post.setContent(content);
        post.setCategory(category);
        post.setTransportType(transportType);
        post.setAnonymous(anonymous);
        post.setGuestId(guestId == null ? "guest_seed" : guestId);
        post.setAuthorName(anonymous ? "匿名茶客" : GuestNames.fromGuestId(guestId));
        post.setImages(null);
        post.setCreatedAt(LocalDateTime.now().minusHours(hoursAgo));
        post.setViewCount(viewCount);
        postRepository.save(post);
    }
}
