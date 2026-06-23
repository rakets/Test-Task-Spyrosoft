# Test-Task-Spyrosoft

<p align="center">
<img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk" alt="Java" />
<img src="https://img.shields.io/badge/Maven-4.0.0-C71A36?style=for-the-badge&logo=apachemaven" alt="Maven" />
<img src="https://img.shields.io/badge/Spring_Boot-4.1.0-green?style=for-the-badge&logo=springboot" alt="Spring Boot" />
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

---

* **Backend application to analyze the UK energy mix and determine the optimal time window for charging electric vehicles (EV).
* **Frontend Repository (React): https://github.com/rakets/Test-Task-Spyrosoft-Frontend

---

## 📑 Table of Contents

* [Tech Stack](#-tech-stack)
* [How to Run the Project](#-how-to-run-the-project)
* [REST	API	endpoints](#-rest-api-endpoints)

---

## 🏗 Tech Stack

* **Java:** 21
* **Spring Boot:** 4.1.0
* **Apache-Maven:** 4.0.0
* **Docker**

---

## 🚀 How to Run the Project

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/rakets/Test-Task-Spyrosoft.git
    ```
2.  **Go to the project folder:**
    ```bash
    cd Test-Task-Spyrosoft
    ```
    ```bash
    cd test-task
    ```
3.  **Build the project:**
    ```bash
    mvn clean install
    ```
6.  **Run the project:**
    ```bash
    mvn spring-boot:run
    ```
    Server will be available at `http://localhost:8080`.

---

## 🚀 REST	API	endpoints

1. **Energy mix download (3 days):**

   **Method:** `GET`
   
   **Endpoint:**
    
    ```bash
    http://localhost:8080/api/energy/mix
    ```
    
    **Request Body Example:**
    
    ```json
    [
        {
            "date": "2026-06-23",
            "averageMix": {
                "biomass": 5.59,
                "other": 0.0,
                "hydro": 0.0,
                "imports": 10.91,
                "gas": 36.28,
                "coal": 0.0,
                "solar": 14.54,
                "nuclear": 10.93,
                "wind": 21.75
            },
            "cleanEnergyPercent": 52.81
        },
        {
            "date": "2026-06-24",
            "averageMix": {
                "biomass": 6.13,
                "other": 0.0,
                "hydro": 0.0,
                "imports": 13.26,
                "gas": 33.9,
                "coal": 0.0,
                "solar": 19.92,
                "nuclear": 11.99,
                "wind": 14.79
            },
            "cleanEnergyPercent": 52.82
        },
        {
            "date": "2026-06-25",
            "averageMix": {
                "biomass": 7.7,
                "other": 0.0,
                "hydro": 0.0,
                "imports": 16.3,
                "gas": 37.8,
                "coal": 0.0,
                "solar": 0.0,
                "nuclear": 14.1,
                "wind": 23.9
            },
            "cleanEnergyPercent": 45.7
        }
    ]
    ```

2. **Determining the optimal loading window:**

    **Method:** `GET`
    
    **Endpoint:**
    
    ```bash
    http://localhost:8080/api/energy/optimal-window/{1-6}
    ```
    
    **Request Body Example:**
    
    ```json
    {
        "start": "2026-06-23T12:30:00Z",
        "end": "2026-06-23T13:30:00Z",
        "avgCleanEnergyPercentage": 70.2
    }
    ```
---
