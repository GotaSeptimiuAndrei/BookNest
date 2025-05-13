package backend.service;

import backend.dto.request.CommunityRequest;
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

	public Community createCommunity(CommunityRequest communityRequest) {
		var author = authorRepository.findById(communityRequest.getAuthorId())
			.orElseThrow(() -> new CommunityException("Author not found with ID: " + communityRequest.getAuthorId()));

		communityRepository.findByAuthorAuthorId(communityRequest.getAuthorId()).ifPresent(existingCommunity -> {
			throw new CommunityException("This author already has a community");
		});

		String photoUrl = saveFileToS3Bucket(s3Client, bucketName, communityRequest.getPhoto());

		Community newCommunity = new Community();
		newCommunity.setAuthor(author);
		newCommunity.setName(communityRequest.getName());
		newCommunity.setDescription(communityRequest.getDescription());
		newCommunity.setPhoto(photoUrl);

		return communityRepository.save(newCommunity);
	}

	public Community updateCommunity(CommunityRequest communityRequest) {
		Community existingCommunity = communityRepository.findByAuthorAuthorId(communityRequest.getAuthorId())
			.orElseThrow(() -> new CommunityException(
					"No existing community found for author with ID: " + communityRequest.getAuthorId()));

		String photoUrl = saveFileToS3Bucket(s3Client, bucketName, communityRequest.getPhoto());

		existingCommunity.setName(communityRequest.getName());
		existingCommunity.setDescription(communityRequest.getDescription());
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

	public boolean authorHasCommunity(Long authorId) {
		return communityRepository.existsByAuthorAuthorId(authorId);
	}

}
