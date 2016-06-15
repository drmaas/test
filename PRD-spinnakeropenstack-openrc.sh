#!/bin/bash

export OS_AUTH_URL=https://openstack.target.com:5000/v2.0
export OS_USERNAME=z001mj7
echo "Please enter your OpenStack Password: "
read -sr OS_PASSWORD_INPUT
export OS_PASSWORD=$OS_PASSWORD_INPUT

gradle clean run --info
