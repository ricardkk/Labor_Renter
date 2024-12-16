package ufersa.com.br.labor_renter.domain.services;

import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ufersa.com.br.labor_renter.api.dto.requests.JobRequest;
import ufersa.com.br.labor_renter.api.dto.responses.JobResponse;
import ufersa.com.br.labor_renter.domain.entities.Address;
import ufersa.com.br.labor_renter.domain.entities.Job;
import ufersa.com.br.labor_renter.domain.entities.UserWorker;
import ufersa.com.br.labor_renter.domain.repositories.AddressRepository;
import ufersa.com.br.labor_renter.domain.repositories.JobRepository;
import ufersa.com.br.labor_renter.domain.repositories.UserWorkerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserWorkerRepository userWorkerRepository;
    private final AddressRepository addressRepository;

    public JobService(JobRepository repository, JobRepository jobRepository, UserWorkerRepository userWorkerRepository, AddressRepository addressRepository) {
        this.jobRepository = jobRepository;
        this.userWorkerRepository = userWorkerRepository;
        this.addressRepository = addressRepository;
    }

    public List<JobResponse> findAll() {
        List<Job> response = jobRepository.findAll();

        return response.stream().map(JobResponse::new).collect(Collectors.toList());
    }

    public JobResponse findById(Long id) {
        Job j = jobRepository.findById(id)
                .orElseThrow(() -> new DataIntegrityViolationException("Id não encontrado"));
        return new JobResponse(j);
    }

    public JobResponse create(JobRequest request) {
        UserWorker worker = userWorkerRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException("Trabalhador não encontrado"));

        Address location = addressRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));

        Job job = new Job();
        job.setWorker(worker);
        job.setLocation(location);
        job.setDescription(request.getDescription());
        job.setAvaliation(0.0);

        Job savedJob = jobRepository.save(job);

        return new JobResponse(savedJob);
    }

    public void delete(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabalho com ID " + id + " não encontrado"));

        jobRepository.delete(job);
    }

    public JobResponse update(Long id, JobRequest request) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Trabalho com ID " + id + " não encontrado"));

        UserWorker worker = userWorkerRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new IllegalArgumentException("Trabalhador com ID " + request.getWorkerId() + " não encontrado"));

        Address location = addressRepository.findById(request.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Endereço com ID " + request.getLocationId() + " não encontrado"));

        existingJob.setLocation(location);
        existingJob.setDescription(request.getDescription());

        return new JobResponse(jobRepository.save(existingJob));
    }




}