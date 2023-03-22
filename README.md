# 스프링배치

# Spring Batch란?

대용량 일괄처리를 위한 오픈 소스 프레임워크이며 최신 엔터프라이즈 시스템에서 흔히 볼 수 있는 강력한 배치 애플리케이션을 개발할 수 있도록 설계된 가볍고 포괄적인 솔루션

# Spring Batch를 사용하는 이유
- 데이터를 읽고/쓰고 개발자가 별도로 개발을 해야하는 풍부한 기능들을 제공
    - 개발자는 비즈니스 로직에 집중할 수 있다.
    - XML, DB, JSON등 다양한 형태로 데이터를 읽기/쓰기 할 수 있음
- 일관성된 코드
    - 배치 작업은 순서가 정형화 되어 있기 때문에 스프링 배치를 활용하여 코드를 작성하면 일관성된 코드를 작성할 수 있음
    - 일관성된 코드는 유지보수 와 지속적인 개발 작업을 할때도 확장성에 유리 함
    - 트랜잭션/롤백 개념도 스프링 배치에서는 고려가 되어 있다.
- 스프링 프레임워크와 호환 가능
    - DI, AOP, 처비스 추상화 등 스프링의 요소들을 모두 사용할 수 있다.

# 스프링 배치의 구조
![스프링배치](https://github.com/90mansik/springBatchTutorial/blob/master/file/springBatchArchitecture.png)
- JobRepository
    - 수행되는 Job에 대한 정보를 담고 있는 저장소로 배치작업의 지속성 메커니즘
    - Spring Batch에서 JobExecution와 StepExecution 등과 같은 지속성을 가진 정보의 기본 CRUD작업에 사용
- JobLauncher
    - Spring Batch Job을 실행시키는 역할
- Job
    - 배치 처리 과정을 하나의 단위로 만들어 놓은 객체
    - ex) 이메일 발송 배치, 회원등급 업데이트 배치,  휴면계정 처리 배치
- Step
    - 실제 배치 처리를 정의하고 제어하는데 필요한 정보가 있는 객체
    - ItemReader(읽기) ,ItemProcessor(처리) ,ItemWriter(쓰기) 단계로 이루어진다.

------
# Hello, world 실행시키기

```java
@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchTutorialApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchTutorialApplication.class, args);
	}

}
```
- 배치처리를 위해 @EnableBatchProcessing 어노테이션 추가
- @EnableBatchProcessing
  - 스프링 배치가 제공하는 어노테이션으로, 배치 인프라스트럭처를 부트스트랩하는데 사용
  - 배치 인프라스트럭처를 위한 대부분의 스프링 빈 정의를 제공
    - JobRepository, JobLauncher ,JobExplorer 등을 제공


## HelloWorldJobConfig.java 추가

```java
@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job helloWorldJob(){
        return jobBuilderFactory.get("helloWorldJob")
                .incrementer(new RunIdIncrementer())
                .start(helloWorldStep())
                .build();
    }

    @JobScope
    @Bean
    public Step helloWorldStep() {
        return stepBuilderFactory.get("helloWorldStep")
                .tasklet(helloWorldTasklet())
                .build();
    }

    @StepScope
    @Bean
    public Tasklet helloWorldTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello World Spring Batch");
                return RepeatStatus.FINISHED;
            }
        };
    }

}
```
- JobBuilderFactory 
  - JobBuilder를 생성하는 팩토리 클래스로 get 메서드를 제공
  - JobBuilder가 jobName을 잡의 이름으로 하여 잡을 생성하도록 구성
    - JobBuilderFactory.get("jobName")
- Job 
  - JobScope : 스프링 컨테이너가 Spring 컨테이너를 통해 지정된 Job의 실행시점에 해당 컴포넌트를 Spring Bean으로 생성
- Step
  - StepScope : 스프링 컨테이너가 Step의 실행시점에 해당 컴포넌트를 Spring Bean으로 생성
- TaskLet
  - 사용자가 작성한 코드를 Tasklet Step 처럼 실행하는 방식
  - 일반 POJO를 Step으로 활용 가능

# Spring Batch Listener

## Listener의 역할

- 배치 Job, Step 실행 전후에 상태확인 및 어떠한 일을 처리할 수 있게하는 Interceptor 개념의 클래스

## Listener의 종류

- Job Listener
  - JobExecutionListener은 Job의 실행 전후에 일을 처리할때 사용
  - beforeJob, afterJob 메서드를 갖는다.
- Step Listener
  - StepExecutionListener은 Step의 실행 전후에 일을 처리할때 사용
  - beforeStep, afterStep 메서드를 갖는다.
  - ChunkListener : 추후 정리
  - ItemReaderListener : 추후정리
  - ItemWriterListener : 추후 정리
  - ItemProcessorListener : 추후 정리


# Job 실행전에 Log를 찍어주는 리스너를 추가하기

### JobLoggerListener.java 추가

```java
@Slf4j
public class JobLoggerListener implements JobExecutionListener {

    private static String BEFORE_MESSAGE = "{} Job is running";

    private static String AFTER_MESSAGE = "{} Job is Done. (Status: {})";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info(BEFORE_MESSAGE, jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info(AFTER_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());

        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.info("Job is Fail");
        }
    }
}
```

- JobExecutionListener 인터페이스를 상속 받으면 아래 메소드가 오버라이드 된다.
  - beforeJob
    - 잡 생명주기에서 가장 먼저 실행
  - afterJob
    - 잡 생명주기에서 가장 나중에 실행
    - 잡의 완료 상태에 관계없이 호출
    - 잡의 종료 상태에 따라 분기 처리 가능
- beforeJob에 job이 시작되기 전에 호출 시킬 메시지를 작성
- afterJob에 job이 종료 될때 호출시킬 메시지를 작성
- afterJob에 job의 상태에 따라 분기 처리가 가능

### JobLoggerListener.java 추가

```java
@Configuration
@RequiredArgsConstructor
public class JobListenerConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobListenerJob(Step jobListenerStep) {
        return jobBuilderFactory.get("jobListenerJob")
                .incrementer(new RunIdIncrementer())
                .listener(new JobLoggerListener())
                .start(jobListenerStep)
                .build();
    }

    @JobScope
    @Bean
    public Step jobListenerStep(Tasklet jobListenerTasklet) {
        return stepBuilderFactory.get("jobListenerStep")
                .tasklet(jobListenerTasklet)
                .build();
    }

    @StepScope
    @Bean
    public Tasklet jobListenerTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("jobListener Spring Batch");
                return RepeatStatus.FINISHED;
                //throw new Exception("fail!!!");
            }
        };
    }

}
```

- JobBuilderFactory의 .listener를 사용해서 위에서 만들어준 JobLoggerListener을 등록 해준다.
