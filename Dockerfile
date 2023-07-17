FROM nginx:alpine

ARG HTML_DIR

COPY ${HTML_DIR} /usr/share/nginx/html