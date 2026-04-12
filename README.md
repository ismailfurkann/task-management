# Task Management System

Full-stack task and project management platform built with **Spring Boot** and **React + TypeScript**.

The system is designed to support team collaboration through a scalable backend architecture and a modular frontend structure.

## Features

- Project and task management
- Kanban workflow (To Do / In Progress / Done)
- Role-based access control (RBAC)
- Activity tracking system
- Task comments, labels, and attachments
- Member management

## Tech Stack

**Backend:** Spring Boot, Spring Security, JPA, Hibernate  
**Frontend:** React, TypeScript  
**Database:** PostgreSQL  

## Architecture

The backend is designed using a layered architecture (Controller → Service → Repository) combined with domain-driven modularization. Business logic is encapsulated within service layers, while persistence concerns are isolated in repository components using JPA/Hibernate.

The system is organized around domain modules (e.g., task, project, member, activity), enabling clear separation of concerns, improved maintainability, and scalability.

The frontend is built with React + TypeScript using a component-based architecture, supported by reusable UI components and domain-specific custom hooks for state and logic management.

## Status

Actively developed and improved.
