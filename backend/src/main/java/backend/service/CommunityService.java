package backend.service;

import backend.dto.CommunityDTO;
import backend.exception.CommunityException;
import backend.model.Community;
import backend.repository.AuthorRepository;
import backend.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import static backend.utils.S3Utils.saveFileToS3Bucket;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

	private final CommunityRepository communityRepository;

	private final AuthorRepository authorRepository;

	private final S3Client s3Client;

	private final String bucketName;

	public List<Community> getAllCommunities() {
		return communityRepository.findAll();
	}

	public Community createCommunity(CommunityDTO communityDTO) {
		var author = authorRepository.findById(communityDTO.getAuthorId())
			.orElseThrow(() -> new CommunityException("Author not found with ID: " + communityDTO.getAuthorId()));

		communityRepository.findByAuthorAuthorId(communityDTO.getAuthorId()).ifPresent(existingCommunity -> {
			throw new CommunityException("This author already has a community");
		});

		String photoUrl = saveFileToS3Bucket(s3Client, bucketName, communityDTO.getPhoto());

		Community newCommunity = new Community();
		newCommunity.setAuthor(author);
		newCommunity.setName(communityDTO.getName());
		newCommunity.setDescription(communityDTO.getDescription());
		newCommunity.setPhoto(photoUrl);

		return communityRepository.save(newCommunity);
	}

	public Community updateCommunity(CommunityDTO communityDTO) {
		Community existingCommunity = communityRepository.findByAuthorAuthorId(communityDTO.getAuthorId())
			.orElseThrow(() -> new CommunityException(
					"No existing community found for author with ID: " + communityDTO.getAuthorId()));

		String photoUrl = saveFileToS3Bucket(s3Client, bucketName, communityDTO.getPhoto());

		existingCommunity.setName(communityDTO.getName());
		existingCommunity.setDescription(communityDTO.getDescription());
		existingCommunity.setPhoto(photoUrl);

		return communityRepository.save(existingCommunity);
	}

	public Community getCommunityByAuthor(Long authorId) {
		return communityRepository.findByAuthorAuthorId(authorId)
			.orElseThrow(() -> new CommunityException("No community found for author ID: " + authorId));
	}

	public Community getCommunityById(Long communityId) {
		return communityRepository.findById(communityId)
			.orElseThrow(() -> new CommunityException("No community found with ID: " + communityId));
	}

}
