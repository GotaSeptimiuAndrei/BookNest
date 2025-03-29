package backend.unit.serviceTests;

import backend.dto.CommunityDTO;
import backend.exception.CommunityException;
import backend.model.Author;
import backend.model.Community;
import backend.repository.AuthorRepository;
import backend.repository.CommunityRepository;
import backend.service.CommunityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommunityServiceTest {

	@Mock
	private CommunityRepository communityRepository;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private S3Client s3Client;

	private CommunityService communityService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		String bucketName = "bucket-community";
		communityService = new CommunityService(communityRepository, authorRepository, s3Client, bucketName);
	}

	@Test
	void testGetAllCommunities() {
		Community community1 = new Community();
		community1.setCommunityId(1L);
		community1.setName("Community One");

		Community community2 = new Community();
		community2.setCommunityId(2L);
		community2.setName("Community Two");

		when(communityRepository.findAll()).thenReturn(List.of(community1, community2));

		List<Community> result = communityService.getAllCommunities();

		assertThat(result).hasSize(2);
		assertThat(result.get(0).getName()).isEqualTo("Community One");
		assertThat(result.get(1).getName()).isEqualTo("Community Two");

		verify(communityRepository).findAll();
	}

	@Test
	void testCreateCommunity_Success() {
		Author author = new Author();
		author.setAuthorId(10L);
		author.setFullName("John Doe");

		MockMultipartFile photoFile = new MockMultipartFile("photo", "test-photo.jpg", "image/jpeg",
				"DummyPhotoContent".getBytes());
		CommunityDTO dto = new CommunityDTO();
		dto.setAuthorId(author.getAuthorId());
		dto.setName("My Community");
		dto.setDescription("A nice place for discussion");
		dto.setPhoto(photoFile);

		when(authorRepository.findById(author.getAuthorId())).thenReturn(Optional.of(author));
		when(communityRepository.findByAuthorAuthorId(author.getAuthorId())).thenReturn(Optional.empty());

		Community savedCommunity = new Community();
		savedCommunity.setCommunityId(1L);
		savedCommunity.setAuthor(author);
		savedCommunity.setName(dto.getName());
		savedCommunity.setDescription(dto.getDescription());
		savedCommunity.setPhoto("https://s3.amazonaws.com/bucket-community/test-photo.jpg");

		when(communityRepository.save(any(Community.class))).thenReturn(savedCommunity);

		Community result = communityService.createCommunity(dto);

		assertThat(result).isNotNull();
		assertThat(result.getCommunityId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("My Community");
		assertThat(result.getPhoto()).contains("test-photo.jpg");

		verify(authorRepository).findById(author.getAuthorId());
		verify(communityRepository).findByAuthorAuthorId(author.getAuthorId());
		verify(communityRepository).save(any(Community.class));
	}

	@Test
	void testCreateCommunity_ExistingCommunityThrowsException() {
		// Suppose the author has an existing community
		Author author = new Author();
		author.setAuthorId(10L);

		Community existingCommunity = new Community();
		existingCommunity.setCommunityId(1L);
		existingCommunity.setAuthor(author);
		existingCommunity.setName("Old Community");

		// DTO
		CommunityDTO dto = new CommunityDTO();
		dto.setAuthorId(author.getAuthorId());
		dto.setName("New Community");

		when(authorRepository.findById(author.getAuthorId())).thenReturn(Optional.of(author));
		when(communityRepository.findByAuthorAuthorId(author.getAuthorId())).thenReturn(Optional.of(existingCommunity));

		assertThrows(CommunityException.class, () -> communityService.createCommunity(dto));

		verify(authorRepository).findById(author.getAuthorId());
		verify(communityRepository).findByAuthorAuthorId(author.getAuthorId());
		verify(communityRepository, never()).save(any());
	}

	@Test
	void testCreateCommunity_AuthorNotFound() {
		CommunityDTO dto = new CommunityDTO();
		dto.setAuthorId(999L);
		dto.setName("DoesNotMatter");

		when(authorRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CommunityException.class, () -> communityService.createCommunity(dto));

		verify(authorRepository).findById(999L);
		verify(communityRepository, never()).findByAuthorAuthorId(999L);
		verify(communityRepository, never()).save(any());
	}

	@Test
	void testUpdateCommunity_Success() {
		Author author = new Author();
		author.setAuthorId(10L);

		Community existingCommunity = new Community();
		existingCommunity.setCommunityId(1L);
		existingCommunity.setAuthor(author);
		existingCommunity.setName("Old Name");

		CommunityDTO dto = new CommunityDTO();
		dto.setAuthorId(author.getAuthorId());
		dto.setName("Updated Name");
		dto.setDescription("Updated Description");
		MockMultipartFile photoFile = new MockMultipartFile("photo", "updated-photo.jpg", "image/jpeg",
				"UpdatedPhotoContent".getBytes());
		dto.setPhoto(photoFile);

		when(communityRepository.findByAuthorAuthorId(dto.getAuthorId())).thenReturn(Optional.of(existingCommunity));

		Community updatedCommunity = new Community();
		updatedCommunity.setCommunityId(existingCommunity.getCommunityId());
		updatedCommunity.setAuthor(author);
		updatedCommunity.setName(dto.getName());
		updatedCommunity.setDescription(dto.getDescription());
		updatedCommunity.setPhoto("https://s3.amazonaws.com/bucket-community/updated-photo.jpg");

		when(communityRepository.save(existingCommunity)).thenReturn(updatedCommunity);

		Community result = communityService.updateCommunity(dto);

		assertThat(result.getCommunityId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("Updated Name");
		assertThat(result.getDescription()).isEqualTo("Updated Description");
		assertThat(result.getPhoto()).contains("updated-photo.jpg");

		verify(communityRepository).findByAuthorAuthorId(dto.getAuthorId());
		verify(communityRepository).save(existingCommunity);
	}

	@Test
	void testUpdateCommunity_NoExistingCommunity() {
		CommunityDTO dto = new CommunityDTO();
		dto.setAuthorId(999L);
		dto.setName("New Name");

		when(communityRepository.findByAuthorAuthorId(999L)).thenReturn(Optional.empty());

		assertThrows(CommunityException.class, () -> communityService.updateCommunity(dto));

		verify(communityRepository).findByAuthorAuthorId(999L);
		verify(communityRepository, never()).save(any());
	}

	@Test
	void testGetCommunityByAuthor_Found() {
		Author author = new Author();
		author.setAuthorId(10L);

		Community community = new Community();
		community.setCommunityId(1L);
		community.setAuthor(author);
		community.setName("My Community");

		when(communityRepository.findByAuthorAuthorId(10L)).thenReturn(Optional.of(community));

		Community result = communityService.getCommunityByAuthor(10L);

		assertThat(result.getCommunityId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("My Community");

		verify(communityRepository).findByAuthorAuthorId(10L);
	}

	@Test
	void testGetCommunityByAuthor_NotFound() {
		when(communityRepository.findByAuthorAuthorId(999L)).thenReturn(Optional.empty());

		assertThrows(CommunityException.class, () -> communityService.getCommunityByAuthor(999L));

		verify(communityRepository).findByAuthorAuthorId(999L);
	}

	@Test
	void testGetCommunityById_Found() {
		Community community = new Community();
		community.setCommunityId(1L);
		community.setName("My Community");

		when(communityRepository.findById(1L)).thenReturn(Optional.of(community));

		Community result = communityService.getCommunityById(1L);

		assertThat(result.getCommunityId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("My Community");

		verify(communityRepository).findById(1L);
	}

	@Test
	void testGetCommunityById_NotFound() {
		when(communityRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(CommunityException.class, () -> communityService.getCommunityById(999L));

		verify(communityRepository).findById(999L);
	}

}
