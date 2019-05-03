# @Author Azazell
# This file is used to test mod in dockerized container space
# If you don't want to use dockerized space to run this mod, then just ignore this file

FROM debian

#-- PRE LOAD --#
# Prepare apt
RUN apt-get -y update

# Install Java
RUN apt-get -y install openjdk-8-jre
RUN apt-get -y install openjdk-8-jdk

# Install wget
RUN apt-get -y install wget

# Install unzip
RUN apt-get -y install unzip

# Install git
RUN apt-get -y install git-core
#-- PRE LOAD --#

# Copy repository
COPY . AI

# Select directory
WORKDIR AI