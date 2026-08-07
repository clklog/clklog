FROM nginx:1.23

ARG HTML_DIR

COPY ${HTML_DIR} /usr/share/nginx/html