package edu.utah.cs.uparknow.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.stereotype.Service;
import edu.utah.cs.uparknow.exception.ResourceNotFoundException;
import edu.utah.cs.uparknow.model.Managers;
import edu.utah.cs.uparknow.repository.ManagersRepository;
import edu.utah.cs.uparknow.util.JwtUtil;
import java.security.SecureRandom;

@Service
public class ManagersService {

    @Autowired
    private ManagersRepository managersRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<Managers> getAllManagers() {
        return managersRepository.findAll();
    }

    public Optional<Managers> getManagerById(Integer id) {
        return managersRepository.findById(id);
    }

    public Managers createManager(Managers manager) {
        if (manager.getManaPassword() != null && !manager.getManaPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(manager.getManaPassword());
            manager.setManaPassword(hashed);
        }
        return managersRepository.save(manager);
    }

    public Managers updateManager(Integer id, Managers managerDetails) {
        Managers manager = managersRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Manager not found for this id :: " + id));

        manager.setManaName(managerDetails.getManaName());
        if (managerDetails.getManaPassword() != null && !managerDetails.getManaPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(managerDetails.getManaPassword());
            manager.setManaPassword(hashed);
        }

        return managersRepository.save(manager);
    }

    public void deleteManager(Integer id) {
        Managers manager = managersRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Manager not found for this id :: " + id));
        managersRepository.delete(manager);
    }

    // ========== Token ==========
    public Optional<Managers> findByUsername(String username) {
        return managersRepository.findByManaUsername(username);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String generateToken(Managers manager) {

        //System.out.println("Generating token for manager: " + manager.getManaUsername());

        String token = JwtUtil.generateToken(manager);

        //System.out.println("Generated token: " + token);
        
        manager.setManaToken(token);
        managersRepository.save(manager);

        //System.out.println("Token saved to database for manager: " + manager.getManaUsername());
        
        return token;
    }

    public boolean validateToken(String token) {
        if (!JwtUtil.validateToken(token)) {
            return false;
        }

        String username = JwtUtil.getUsernameFromToken(token);
        Optional<Managers> managerOpt = findByUsername(username);
        if (!managerOpt.isPresent()) {
            return false;
        }

        Managers manager = managerOpt.get();
        return token.equals(manager.getManaToken());
    }

    @Autowired
    private MailService mailService;
    public boolean resetPasswordFor(String username) {
        Optional<Managers> managerOpt = findByUsername(username);
        if (!managerOpt.isPresent()) {
            return false;
        }

        Managers manager = managerOpt.get();
        String tempPwd = generateRandomPassword(13);
        String hashed = passwordEncoder.encode(tempPwd);
        manager.setManaPassword(hashed);
        managersRepository.save(manager);

        String subject = "Your Password Reset";
        String content = String.format("Hello %s,\nYour password (180 days) is: %s\n",
                manager.getManaName(), tempPwd);

       mailService.sendSimpleMail(username, subject, content);
        return true;
    }

    private String generateRandomPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "!@#$%^&*()-_=+";

        String all = upper + lower + digits + specials;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(specials.charAt(random.nextInt(specials.length())));

        for (int i = 4; i < length; i++) {
            sb.append(all.charAt(random.nextInt(all.length())));
        }

        return shuffleString(sb.toString(), random);
    }

    private String shuffleString(String input, SecureRandom random) {
        List<Character> characters = input.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.toList());
        StringBuilder output = new StringBuilder(input.length());
        while (!characters.isEmpty()) {
            output.append(characters.remove(random.nextInt(characters.size())));
        }
        return output.toString();
    }
}
