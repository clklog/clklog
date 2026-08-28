FROM nginx:1.29-alpine

ARG HTML_DIR

COPY ${HTML_DIR} /usr/share/nginx/html
