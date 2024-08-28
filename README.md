# Distributed-Continuum-Orchestrator-DCO

## Overview

Welcome to the **Distributed Continuum Orchestrator (DCO)** project repository. This project is part of the PhD application assignment for the Distributed and Parallel Systems Group at the University of Innsbruck. The goal of this project is to design and implement a distributed system that leverages state machine models to manage a complex application scenario in a highly available and fault-tolerant environment.

### Project Structure

This repository is organized into the following key sections:

- **src/**: Contains the Java source code for the implementation of the distributed state machines and the runtime environment.
- **docs/**: Includes detailed documentation about the project, including the design choices, theoretical background, and how-to guides.
- **diagrams/**: Contains state transition diagrams and other visual aids that represent the state machines and their interactions.
- **tests/**: Contains unit tests and integration tests to ensure the correctness and reliability of the implementation.
- **scripts/**: Contains scripts for setting up the Docker environment and orchestrating the distributed application across multiple nodes.

### Task 1: Theoretical Exploration

This task involves a detailed theoretical analysis of several key concepts in distributed systems, including:

1. **Concurrency and Parallelism in Programming Languages**:

   - Analysis of how languages like Java and Python handle concurrency and parallelism.
   - Discussion on the impact of these features on the design and implementation of distributed systems.

2. **Leader Election Algorithms**:

   - Description and analysis of an efficient leader election algorithm.
   - Examination of its time and space complexity, and its effectiveness under different conditions.

3. **High Availability and Fault Tolerance Best Practices**:

   - Exploration of best practices for achieving high availability and fault tolerance.
   - Examples of strategies and their impact on system reliability.

4. **Communication Strategy for Isolated Networks**:

   - Design of a secure communication strategy between isolated private networks using proxies, VPNs, or relay services.

5. **Understanding of the Computing Continuum**:
   - In-depth description of the Cloud-Edge-IoT continuum.
   - Analysis of the purpose of each layer and examples of common use cases.

### Task 2: Practical Implementation

This task focuses on the design and implementation of a distributed state machine runtime environment and the deployment of a real-world application scenario. The project is divided into the following parts:

#### Part 1: State Machine Model

- **Objective**: Design a versatile state machine model capable of representing arbitrary state machines.
- **Details**:
  - The model should support various components such as states, events, actions, transitions, and functions.
  - The design should allow serialization for future use in the runtime environment.
- **Documentation**: Detailed explanation of the model, design choices, and potential extensions for enhanced functionality.

#### Part 2: Distributed Application

- **Objective**: Apply the state machine model to an application scenario managing lights and gates at a railroad crossing.
- **Details**:
  - Implementation of the necessary functions to support the application's requirements.
  - Description of the application using the state machine model.
  - Creation of diagrams illustrating state transitions and interactions among components.
- **Documentation**: Justification of design choices and discussion on the model's scalability, adaptability, and efficiency.

#### Part 3: Runtime Environment

- **Objective**: Implement a distributed runtime environment in Java that can execute the application across at least three resources.
- **Details**:
  - The runtime should use orchestration tools like Docker Swarm, Nomad, or Consul to manage the distributed resources.
  - The state machine behavior should be defined by the model and application description, not hardcoded.
- **Documentation**: Comprehensive details on the implementation, orchestration environment, and instructions for deployment and execution.

### Getting Started

To get started with the project, follow these steps:

1. **Clone the repository**:

   ```bash
   git clone https://github.com/yourusername/distributed-continuum-orchestrator.git
   cd distributed-continuum-orchestrator
   ```

2. **Set up the development environment**:

   - Ensure you have Java installed (version 11 or higher).
   - Install Docker for container orchestration.
   - (Optional) Set up an IDE like IntelliJ IDEA for easier development.

3. **Build the project**:

   ```bash
   ./gradlew build
   ```

4. **Run the tests**:

   ```bash
   ./gradlew test
   ```

5. **Deploy the distributed application**:
   - Follow the instructions in the `docs/deployment.md` file to deploy the application across multiple nodes using Docker.

### Contribution Guidelines

We welcome contributions to improve the DCO project. Please follow the guidelines below:

- Fork the repository and create a new branch for your feature or bugfix.
- Make sure to add appropriate tests and documentation.
- Submit a pull request with a detailed description of your changes.

### License

This project is licensed under the MIT License - see the `LICENSE` file for details.

### Contact

If you have any questions or need further assistance, feel free to open an issue in the repository or contact the project maintainer at [fjordoneza@unal.edu.co].
